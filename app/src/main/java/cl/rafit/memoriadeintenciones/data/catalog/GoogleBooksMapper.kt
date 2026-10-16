package cl.rafit.memoriadeintenciones.data.catalog

import cl.rafit.memoriadeintenciones.domain.CatalogItem
import cl.rafit.memoriadeintenciones.domain.CatalogProvider
import kotlinx.serialization.Serializable

data class GoogleBookVolume(
    val id: String,
    val title: String?,
    val authors: List<String> = emptyList(),
    val publisher: String? = null,
    val publishedDate: String? = null,
    val identifiers: List<BookIdentifier> = emptyList(),
    val thumbnailUrl: String? = null
)

@Serializable
data class BookIdentifier(val type: String, val value: String)

object GoogleBooksMapper {
    fun toCatalogItem(volume: GoogleBookVolume): CatalogItem? {
        val title = volume.title?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        return CatalogItem(
            provider = CatalogProvider.GOOGLE_BOOKS,
            externalId = volume.id,
            title = title,
            contributors = volume.authors.map(String::trim).filter(String::isNotEmpty),
            publisher = volume.publisher?.trim()?.takeIf(String::isNotEmpty),
            publishedYear = volume.publishedDate?.take(4)?.toIntOrNull(),
            isbn = volume.identifiers.firstOrNull { it.type == "ISBN_13" || it.type == "ISBN_10" }?.value,
            thumbnailUrl = volume.thumbnailUrl?.replace("http://", "https://")
        )
    }
}
