package cl.rafit.memoriadeintenciones.data

import cl.rafit.memoriadeintenciones.data.local.LibraryItemDao
import cl.rafit.memoriadeintenciones.data.local.ExperienceDao
import cl.rafit.memoriadeintenciones.data.local.toDomain
import cl.rafit.memoriadeintenciones.data.local.toEntity
import cl.rafit.memoriadeintenciones.domain.LibraryCategory
import cl.rafit.memoriadeintenciones.domain.LibraryItem
import cl.rafit.memoriadeintenciones.domain.LibraryStatus
import cl.rafit.memoriadeintenciones.domain.BookMetadata
import cl.rafit.memoriadeintenciones.domain.ItemExperience
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
}

class RoomLibraryRepository(
    private val dao: LibraryItemDao,
    private val experienceDao: ExperienceDao? = null
) : LibraryRepository {
    override fun observeAll(): Flow<List<LibraryItem>> =
        experienceDao?.let { experiences ->
            combine(dao.observeAll(), experiences.observeAll()) { items, records ->
                val byItem = records.associateBy { it.itemId }
                items.map { it.toDomain(byItem[it.id]?.toDomain()) }
            }
        } ?: dao.observeAll().map { items -> items.map { it.toDomain() } }

    override fun observeByCategory(category: LibraryCategory): Flow<List<LibraryItem>> =
        experienceDao?.let { experiences ->
            combine(dao.observeByCategory(category.name), experiences.observeAll()) { items, records ->
                val byItem = records.associateBy { it.itemId }
                items.map { it.toDomain(byItem[it.id]?.toDomain()) }
            }
        } ?: dao.observeByCategory(category.name).map { items -> items.map { it.toDomain() } }

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
}
