package cl.rafit.memoriadeintenciones.data

import cl.rafit.memoriadeintenciones.data.local.LibraryItemDao
import cl.rafit.memoriadeintenciones.data.local.toDomain
import cl.rafit.memoriadeintenciones.data.local.toEntity
import cl.rafit.memoriadeintenciones.domain.LibraryCategory
import cl.rafit.memoriadeintenciones.domain.LibraryItem
import cl.rafit.memoriadeintenciones.domain.LibraryStatus
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
        now: Long
    ): Long
    suspend fun insert(item: LibraryItem): Long
    suspend fun update(item: LibraryItem)
    suspend fun delete(id: Long)
}

class RoomLibraryRepository(
    private val dao: LibraryItemDao
) : LibraryRepository {
    override fun observeAll(): Flow<List<LibraryItem>> =
        dao.observeAll().map { items -> items.map { it.toDomain() } }

    override fun observeByCategory(category: LibraryCategory): Flow<List<LibraryItem>> =
        dao.observeByCategory(category.name).map { items -> items.map { it.toDomain() } }

    override suspend fun add(
        title: String,
        category: LibraryCategory,
        status: LibraryStatus,
        durationMinutes: Int?,
        notes: String?,
        now: Long
    ): Long {
        val normalizedTitle = title.trim()
        require(normalizedTitle.isNotEmpty()) { "El título no puede estar vacío" }
        require(durationMinutes == null || durationMinutes > 0) { "La duración debe ser mayor que cero" }
        return insert(
            LibraryItem(
                title = normalizedTitle,
                category = category,
                status = status,
                durationMinutes = durationMinutes,
                notes = notes?.trim()?.takeIf { it.isNotEmpty() },
                createdAt = now,
                updatedAt = now
            )
        )
    }

    override suspend fun insert(item: LibraryItem): Long {
        require(item.title.isNotBlank()) { "El título no puede estar vacío" }
        return dao.insert(item.toEntity())
    }

    override suspend fun update(item: LibraryItem) {
        require(item.title.isNotBlank()) { "El título no puede estar vacío" }
        dao.update(item.toEntity())
    }

    override suspend fun delete(id: Long) {
        dao.deleteById(id)
    }
}
