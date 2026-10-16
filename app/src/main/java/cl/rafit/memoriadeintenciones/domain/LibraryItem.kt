package cl.rafit.memoriadeintenciones.domain

enum class LibraryCategory {
    MOVIE,
    SERIES,
    BOOK,
    GAME
}

enum class LibraryStatus {
    TO_CONSUME,
    IN_PROGRESS,
    COMPLETED,
    ABANDONED
}

data class LibraryItem(
    val id: Long = 0,
    val title: String,
    val category: LibraryCategory,
    val status: LibraryStatus = LibraryStatus.TO_CONSUME,
    val recommendationPriority: RecommendationPriority = RecommendationPriority.NORMAL,
    val durationMinutes: Int? = null,
    val notes: String? = null,
    val bookMetadata: BookMetadata? = null,
    val experience: ItemExperience? = null,
    val recommendationFeedback: RecommendationFeedback = RecommendationFeedback(),
    val createdAt: Long,
    val updatedAt: Long
)
