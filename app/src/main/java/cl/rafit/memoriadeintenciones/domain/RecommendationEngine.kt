package cl.rafit.memoriadeintenciones.domain

import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

data class RecommendationPreferences(
    val temporalWeight: Int = 40,
    val priorityWeight: Int = 20,
    val ageWeight: Int = 15,
    val unknownDurationPenalty: Int = 8,
    val acceptedWeight: Int = 4,
    val postponedWeight: Int = 6,
    val categoryAffinity: Map<LibraryCategory, Int> = emptyMap()
)

data class RecommendationReason(
    val text: String,
    val source: Source
) {
    enum class Source {
        TIME,
        PRIORITY,
        AGE,
        AFFINITY,
        FEEDBACK,
        DURATION
    }
}

data class Recommendation(
    val item: LibraryItem,
    val score: Int,
    val reasons: List<RecommendationReason>
)

object RecommendationEngine {
    private const val DAY_MILLIS = 24L * 60L * 60L * 1000L
    private const val AGE_CAP_DAYS = 180L

    fun rank(
        items: List<LibraryItem>,
        availableMinutes: Int,
        asOfMillis: Long,
        preferences: RecommendationPreferences = RecommendationPreferences()
    ): List<Recommendation> {
        require(availableMinutes > 0) { "El tiempo disponible debe ser mayor que cero" }

        return items
            .asSequence()
            .filter { it.status == LibraryStatus.TO_CONSUME }
            .filter { !it.recommendationFeedback.dismissed }
            .map { item -> score(item, availableMinutes, asOfMillis, preferences) }
            .sortedWith(
                compareByDescending<Recommendation> { it.score }
                    .thenByDescending { it.item.durationMinutes?.let { duration -> duration <= availableMinutes } == true }
                    .thenByDescending { it.item.durationMinutes != null }
                    .thenBy { it.item.createdAt }
                    .thenBy { it.item.id }
            )
            .toList()
    }

    private fun score(
        item: LibraryItem,
        availableMinutes: Int,
        asOfMillis: Long,
        preferences: RecommendationPreferences
    ): Recommendation {
        val reasons = mutableListOf<RecommendationReason>()
        val temporalScore = temporalScore(item.durationMinutes, availableMinutes, preferences, reasons)
        val priorityScore = priorityScore(item.recommendationPriority, preferences, reasons)
        val ageScore = ageScore(item.createdAt, asOfMillis, preferences, reasons)
        val affinityScore = preferences.categoryAffinity[item.category].orZero()
        if (affinityScore != 0) {
            reasons += RecommendationReason(
                text = if (affinityScore > 0) "Afinidad local por ${item.category.displayName()}" else "Afinidad local menor por ${item.category.displayName()}",
                source = RecommendationReason.Source.AFFINITY
            )
        }

        val feedbackScore = item.recommendationFeedback.acceptedCount * preferences.acceptedWeight -
            item.recommendationFeedback.postponedCount * preferences.postponedWeight
        if (feedbackScore != 0) {
            reasons += RecommendationReason(
                text = if (feedbackScore > 0) "Tu feedback anterior la favorece" else "Tu feedback anterior la retrasa",
                source = RecommendationReason.Source.FEEDBACK
            )
        }

        return Recommendation(
            item = item,
            score = temporalScore + priorityScore + ageScore + affinityScore + feedbackScore,
            reasons = reasons
        )
    }

    private fun temporalScore(
        durationMinutes: Int?,
        availableMinutes: Int,
        preferences: RecommendationPreferences,
        reasons: MutableList<RecommendationReason>
    ): Int {
        if (durationMinutes == null) {
            reasons += RecommendationReason("Duración no indicada", RecommendationReason.Source.DURATION)
            return -preferences.unknownDurationPenalty
        }
        if (durationMinutes <= availableMinutes) {
            val spareRatio = (availableMinutes - durationMinutes).toDouble() / availableMinutes
            val score = (preferences.temporalWeight * (1.0 - spareRatio / 2.0)).roundToInt()
            reasons += RecommendationReason("Cabe en tus $availableMinutes min", RecommendationReason.Source.TIME)
            return score
        }
        val excess = durationMinutes - availableMinutes
        val penalty = (preferences.temporalWeight * excess.toDouble() / availableMinutes / 2.0).roundToInt()
        reasons += RecommendationReason("Supera tu tiempo disponible", RecommendationReason.Source.TIME)
        return -min(preferences.temporalWeight / 2, max(1, penalty))
    }

    private fun priorityScore(
        priority: RecommendationPriority,
        preferences: RecommendationPreferences,
        reasons: MutableList<RecommendationReason>
    ): Int = when (priority) {
        RecommendationPriority.HIGH -> {
            reasons += RecommendationReason("Prioridad alta", RecommendationReason.Source.PRIORITY)
            preferences.priorityWeight
        }
        RecommendationPriority.NORMAL -> 0
        RecommendationPriority.LOW -> {
            reasons += RecommendationReason("Prioridad baja", RecommendationReason.Source.PRIORITY)
            -preferences.priorityWeight
        }
    }

    private fun ageScore(
        createdAt: Long,
        asOfMillis: Long,
        preferences: RecommendationPreferences,
        reasons: MutableList<RecommendationReason>
    ): Int {
        val ageDays = max(0L, (asOfMillis - createdAt) / DAY_MILLIS)
        if (ageDays >= 30) reasons += RecommendationReason("Lleva más tiempo pendiente", RecommendationReason.Source.AGE)
        return (preferences.ageWeight * min(AGE_CAP_DAYS, ageDays).toDouble() / AGE_CAP_DAYS).roundToInt()
    }

    private fun Int?.orZero(): Int = this ?: 0

    private fun LibraryCategory.displayName(): String = when (this) {
        LibraryCategory.MOVIE -> "películas"
        LibraryCategory.SERIES -> "series"
        LibraryCategory.BOOK -> "libros"
        LibraryCategory.GAME -> "videojuegos"
    }
}
