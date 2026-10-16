package cl.rafit.memoriadeintenciones.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.rafit.memoriadeintenciones.domain.LibraryCategory
import cl.rafit.memoriadeintenciones.domain.LibraryItem
import cl.rafit.memoriadeintenciones.domain.LibraryStatus
import cl.rafit.memoriadeintenciones.domain.BookMetadata
import cl.rafit.memoriadeintenciones.domain.ItemExperience
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

@Entity(tableName = "library_items")
data class LibraryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val status: String,
    val durationMinutes: Int? = null,
    val notes: String?,
    val bookIsbn: String? = null,
    val bookAuthors: String? = null,
    val bookPublisher: String? = null,
    val bookPublishedDate: String? = null,
    val bookLanguage: String? = null,
    val bookPageCount: Int? = null,
    val bookCoverUrl: String? = null,
    val bookCoverLocalPath: String? = null,
    val bookEdition: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)

fun LibraryItemEntity.toDomain(experience: ItemExperience? = null): LibraryItem = LibraryItem(
    id = id,
    title = title,
    category = LibraryCategory.valueOf(category),
    status = LibraryStatus.valueOf(status),
    durationMinutes = durationMinutes,
    notes = notes,
    bookMetadata = bookIsbn?.let { isbn ->
        BookMetadata(
            isbn = isbn,
            authors = bookAuthors?.let { encoded -> runCatching { Json.decodeFromString<List<String>>(encoded) }.getOrDefault(emptyList()) }.orEmpty(),
            publisher = bookPublisher,
            publishedDate = bookPublishedDate,
            language = bookLanguage,
            pageCount = bookPageCount,
            coverUrl = bookCoverUrl,
            coverLocalPath = bookCoverLocalPath,
            edition = bookEdition
        )
    },
    experience = experience,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun LibraryItem.toEntity(): LibraryItemEntity = LibraryItemEntity(
    id = id,
    title = title,
    category = category.name,
    status = status.name,
    durationMinutes = durationMinutes,
    notes = notes,
    bookIsbn = bookMetadata?.isbn,
    bookAuthors = bookMetadata?.authors?.let(Json::encodeToString),
    bookPublisher = bookMetadata?.publisher,
    bookPublishedDate = bookMetadata?.publishedDate,
    bookLanguage = bookMetadata?.language,
    bookPageCount = bookMetadata?.pageCount,
    bookCoverUrl = bookMetadata?.coverUrl,
    bookCoverLocalPath = bookMetadata?.coverLocalPath,
    bookEdition = bookMetadata?.edition,
    createdAt = createdAt,
    updatedAt = updatedAt
)
