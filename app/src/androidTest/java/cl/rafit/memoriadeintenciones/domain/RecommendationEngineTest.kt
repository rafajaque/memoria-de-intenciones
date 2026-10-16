package cl.rafit.memoriadeintenciones.domain

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecommendationEngineTest {
    private val now = 200L * DAY_MILLIS

    @Test
    fun ranksPendingItemsByFitAndStableTieBreakers() {
        val tooLong = item(id = 1, title = "Larga", duration = 90, createdAt = now - 10 * DAY_MILLIS)
        val exact = item(id = 2, title = "Exacta", duration = 45, createdAt = now - 2 * DAY_MILLIS)
        val shorter = item(id = 3, title = "Corta", duration = 20, createdAt = now - 100 * DAY_MILLIS)
        val inProgress = item(id = 4, title = "Ya iniciada", duration = 10, status = LibraryStatus.IN_PROGRESS, createdAt = now)

        val result = RecommendationEngine.rank(listOf(tooLong, inProgress, shorter, exact), 45, now)

        assertEquals(listOf("Exacta", "Corta", "Larga"), result.map { it.item.title })
        assertTrue(result.first().reasons.any { it.text == "Cabe en tus 45 min" })
    }

    @Test
    fun unknownDurationIsIncludedWithVisibleReason() {
        val result = RecommendationEngine.rank(listOf(item(id = 1, title = "Sin duración", duration = null)), 30, now)

        assertEquals(1, result.size)
        assertTrue(result.single().reasons.any { it.text == "Duración no indicada" })
    }

    @Test
    fun priorityAndFeedbackAffectOrderAndDismissal() {
        val normal = item(id = 1, title = "Normal", duration = 30, priority = RecommendationPriority.NORMAL)
        val high = item(id = 2, title = "Alta", duration = 30, priority = RecommendationPriority.HIGH)
        val dismissed = item(id = 3, title = "Descartada", duration = 30, feedback = RecommendationFeedback(dismissed = true))

        val result = RecommendationEngine.rank(listOf(normal, dismissed, high), 30, now)

        assertEquals(listOf("Alta", "Normal"), result.map { it.item.title })
        assertTrue(result.first().reasons.any { it.text == "Prioridad alta" })
    }

    @Test
    fun equalScoresUseOldestThenId() {
        val older = item(id = 10, title = "Antigua", duration = 30, createdAt = now - DAY_MILLIS)
        val newer = item(id = 2, title = "Nueva", duration = 30, createdAt = now - DAY_MILLIS)

        val result = RecommendationEngine.rank(listOf(newer, older), 30, now)

        assertEquals(listOf("Nueva", "Antigua"), result.map { it.item.title })
    }

    @Test
    fun rejectsNonPositiveTime() {
        assertThrows(IllegalArgumentException::class.java) {
            RecommendationEngine.rank(emptyList(), 0, now)
        }
    }

    private fun item(
        id: Long,
        title: String,
        duration: Int?,
        status: LibraryStatus = LibraryStatus.TO_CONSUME,
        priority: RecommendationPriority = RecommendationPriority.NORMAL,
        feedback: RecommendationFeedback = RecommendationFeedback(),
        createdAt: Long = now
    ) = LibraryItem(
        id = id,
        title = title,
        category = LibraryCategory.MOVIE,
        status = status,
        recommendationPriority = priority,
        durationMinutes = duration,
        recommendationFeedback = feedback,
        createdAt = createdAt,
        updatedAt = createdAt
    )

    private companion object {
        const val DAY_MILLIS = 24L * 60L * 60L * 1000L
    }
}
