package cl.rafit.memoriadeintenciones.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.rafit.memoriadeintenciones.domain.LibraryCategory
import cl.rafit.memoriadeintenciones.domain.LibraryItem
import cl.rafit.memoriadeintenciones.domain.LibraryStatus

@Entity(tableName = "library_items")
data class LibraryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val status: String,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long
)

fun LibraryItemEntity.toDomain(): LibraryItem = LibraryItem(
    id = id,
    title = title,
    category = LibraryCategory.valueOf(category),
    status = LibraryStatus.valueOf(status),
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun LibraryItem.toEntity(): LibraryItemEntity = LibraryItemEntity(
    id = id,
    title = title,
    category = category.name,
    status = status.name,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)
