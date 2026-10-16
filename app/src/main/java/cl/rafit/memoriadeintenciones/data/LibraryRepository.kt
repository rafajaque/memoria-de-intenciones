package cl.rafit.memoriadeintenciones.data

import cl.rafit.memoriadeintenciones.data.local.LibraryItemDao
import cl.rafit.memoriadeintenciones.data.local.ExperienceDao
import cl.rafit.memoriadeintenciones.data.local.RecommendationFeedbackDao
import cl.rafit.memoriadeintenciones.data.local.toDomain
import cl.rafit.memoriadeintenciones.data.local.toEntity
import cl.rafit.memoriadeintenciones.domain.LibraryCategory
import cl.rafit.memoriadeintenciones.domain.LibraryItem
import cl.rafit.memoriadeintenciones.domain.LibraryStatus
import cl.rafit.memoriadeintenciones.domain.BookMetadata
import cl.rafit.memoriadeintenciones.domain.ItemExperience
import cl.rafit.memoriadeintenciones.domain.RecommendationFeedback
import cl.rafit.memoriadeintenciones.domain.RecommendationPriority
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface LibraryRepository {
    fun observeAll(): Flow<List<LibraryItem>>
    fun observeByCategory(category: LibraryCategory): Flow<List<LibraryItem>>
    suspend fun add(
        title: String,
        category: LibraryCategory,
        status: LibraryStatus,
        durationMinutes: Int?,
        notes: String?,
        bookMetadata: BookMetadata?,
        now: Long
    ): Long
    suspend fun insert(item: LibraryItem): Long
    suspend fun edit(
        item: LibraryItem,
        title: String,
        status: LibraryStatus,
        durationMinutes: Int?,
        notes: String?,
        bookMetadata: BookMetadata?,
        now: Long
    )
    suspend fun update(item: LibraryItem)
    suspend fun delete(id: Long)
    suspend fun saveExperience(itemId: Long, experience: ItemExperience)
    suspend fun deleteExperience(itemId: Long)
    suspend fun setRecommendationPriority(itemId: Long, priority: RecommendationPriority)
    suspend fun recordRecommendationAccepted(itemId: Long)
    suspend fun recordRecommendationPostponed(itemId: Long)
    suspend fun recordRecommendationDismissed(itemId: Long)
}

class RoomLibraryRepository(
    private val dao: LibraryItemDao,
    private val experienceDao: ExperienceDao? = null,
    private val recommendationFeedbackDao: RecommendationFeedbackDao? = null
) : LibraryRepository {
    override fun observeAll(): Flow<List<LibraryItem>> = observeItems(dao.observeAll())

    override fun observeByCategory(category: LibraryCategory): Flow<List<LibraryItem>> = observeItems(dao.observeByCategory(category.name))

    private fun observeItems(items: Flow<List<cl.rafit.memoriadeintenciones.data.local.LibraryItemEntity>>): Flow<List<LibraryItem>> {
        val experiences = experienceDao?.observeAll() ?: flowOf(emptyList())
        val feedback = recommendationFeedbackDao?.observeAll() ?: flowOf(emptyList())
        return combine(items, experiences, feedback) { records, experienceRecords, feedbackRecords ->
            val byExperience = experienceRecords.associateBy { it.itemId }
            val byFeedback = feedbackRecords.associateBy { it.itemId }
            records.map { record ->
                record.toDomain(
                    experience = byExperience[record.id]?.toDomain(),
                    recommendationFeedback = byFeedback[record.id]?.toDomain() ?: RecommendationFeedback()
                )
            }
        }
    }

    override suspend fun add(
        title: String,
        category: LibraryCategory,
        status: LibraryStatus,
        durationMinutes: Int?,
        notes: String?,
        bookMetadata: BookMetadata?,
        now: Long
    ): Long {
        val normalizedTitle = title.trim()
        require(normalizedTitle.isNotEmpty()) { "El título no puede estar vacío" }
        require(durationMinutes == null || durationMinutes > 0) { "La duración debe ser mayor que cero" }
        require(category == LibraryCategory.BOOK || bookMetadata == null) { "Los metadatos bibliográficos sólo corresponden a Libros" }
        return insert(
            LibraryItem(
                title = normalizedTitle,
                category = category,
                status = status,
                durationMinutes = durationMinutes,
                notes = notes?.trim()?.takeIf { it.isNotEmpty() },
                bookMetadata = bookMetadata,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    override suspend fun insert(item: LibraryItem): Long {
        require(item.title.isNotBlank()) { "El título no puede estar vacío" }
        return dao.insert(item.toEntity())
    }

    override suspend fun edit(
        item: LibraryItem,
        title: String,
        status: LibraryStatus,
        durationMinutes: Int?,
        notes: String?,
        bookMetadata: BookMetadata?,
        now: Long
    ) {
        val normalizedTitle = title.trim()
        require(normalizedTitle.isNotEmpty()) { "El título no puede estar vacío" }
        require(durationMinutes == null || durationMinutes > 0) { "La duración debe ser mayor que cero" }
        require(item.category == LibraryCategory.BOOK || bookMetadata == null) { "Los metadatos bibliográficos sólo corresponden a Libros" }
        update(
            item.copy(
                title = normalizedTitle,
                status = status,
                durationMinutes = durationMinutes,
                notes = notes?.trim()?.takeIf { it.isNotEmpty() },
                bookMetadata = bookMetadata,
                updatedAt = now
            )
        )
    }

    override suspend fun update(item: LibraryItem) {
        require(item.title.isNotBlank()) { "El título no puede estar vacío" }
        dao.update(item.toEntity())
    }

    override suspend fun delete(id: Long) {
        dao.deleteById(id)
        experienceDao?.deleteByItemId(id)
        recommendationFeedbackDao?.deleteByItemId(id)
    }

    override suspend fun saveExperience(itemId: Long, experience: ItemExperience) {
        require(experience.rating == null || experience.rating in 1..5) { "La valoración debe estar entre 1 y 5" }
        require(experience.startedAt == null || experience.finishedAt == null || experience.finishedAt >= experience.startedAt) {
            "La fecha de término no puede ser anterior al inicio"
        }
        val now = System.currentTimeMillis()
        require(experienceDao != null) { "Las experiencias no están disponibles" }
        experienceDao.upsert(experience.copy(
            platform = experience.platform?.trim()?.takeIf(String::isNotEmpty),
            comment = experience.comment?.trim()?.takeIf(String::isNotEmpty),
            createdAt = if (experience.createdAt == 0L) now else experience.createdAt,
            updatedAt = now
        ).toEntity(itemId))
    }

    override suspend fun deleteExperience(itemId: Long) {
        require(experienceDao != null) { "Las experiencias no están disponibles" }
        experienceDao.deleteByItemId(itemId)
    }

    override suspend fun setRecommendationPriority(itemId: Long, priority: RecommendationPriority) {
        val item = dao.observeAll().first().firstOrNull { it.id == itemId } ?: return
        dao.update(item.copy(recommendationPriority = priority.name, updatedAt = System.currentTimeMillis()))
    }

    override suspend fun recordRecommendationAccepted(itemId: Long) = updateFeedback(itemId) { feedback ->
        feedback.copy(acceptedCount = feedback.acceptedCount + 1, dismissed = false)
    }

    override suspend fun recordRecommendationPostponed(itemId: Long) = updateFeedback(itemId) { feedback ->
        feedback.copy(postponedCount = feedback.postponedCount + 1)
    }

    override suspend fun recordRecommendationDismissed(itemId: Long) = updateFeedback(itemId) { feedback ->
        feedback.copy(dismissed = true)
    }

    private suspend fun updateFeedback(itemId: Long, update: (RecommendationFeedback) -> RecommendationFeedback) {
        val feedbackDao = recommendationFeedbackDao ?: error("Las recomendaciones no están disponibles")
        val current = feedbackDao.observeAll().first().firstOrNull { it.itemId == itemId }?.toDomain() ?: RecommendationFeedback()
        feedbackDao.upsert(update(current).copy(updatedAt = System.currentTimeMillis()).toEntity(itemId))
    }
}
