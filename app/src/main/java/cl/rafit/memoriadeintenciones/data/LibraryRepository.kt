package cl.rafit.memoriadeintenciones.data

import cl.rafit.memoriadeintenciones.data.local.LibraryItemDao
import cl.rafit.memoriadeintenciones.data.local.toDomain
import cl.rafit.memoriadeintenciones.data.local.toEntity
import cl.rafit.memoriadeintenciones.domain.LibraryCategory
import cl.rafit.memoriadeintenciones.domain.LibraryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LibraryRepository {
    fun observeAll(): Flow<List<LibraryItem>>
    fun observeByCategory(category: LibraryCategory): Flow<List<LibraryItem>>
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
