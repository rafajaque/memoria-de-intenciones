package cl.rafit.memoriadeintenciones.data.catalog

import cl.rafit.memoriadeintenciones.domain.CatalogItem
import cl.rafit.memoriadeintenciones.domain.CatalogProvider
import cl.rafit.memoriadeintenciones.domain.BookCatalogProposal
import kotlinx.serialization.Serializable

data class GoogleBookVolume(
    val id: String,
    val title: String?,
    val authors: List<String> = emptyList(),
    val publisher: String? = null,
    val publishedDate: String? = null,
    val language: String? = null,
    val pageCount: Int? = null,
    val identifiers: List<BookIdentifier> = emptyList(),
    val thumbnailUrl: String? = null
)

@Serializable
data class BookIdentifier(val type: String, val value: String)

object GoogleBooksMapper {
    fun toVolume(network: GoogleBooksNetworkVolume): GoogleBookVolume = GoogleBookVolume(
        id = network.id,
        title = network.volumeInfo.title,
        authors = network.volumeInfo.authors,
        publisher = network.volumeInfo.publisher,
        publishedDate = network.volumeInfo.publishedDate,
        language = network.volumeInfo.language,
        pageCount = network.volumeInfo.pageCount,
        identifiers = network.volumeInfo.industryIdentifiers,
        thumbnailUrl = network.volumeInfo.imageLinks?.thumbnail
    )

    fun toBookProposal(volume: GoogleBookVolume): BookCatalogProposal? {
        val title = volume.title?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        return BookCatalogProposal(
            provider = CatalogProvider.GOOGLE_BOOKS,
            externalId = volume.id,
            title = title,
            isbn = volume.identifiers.firstOrNull { it.type == "ISBN_13" || it.type == "ISBN_10" }?.value,
            authors = volume.authors.map(String::trim).filter(String::isNotEmpty),
            publisher = volume.publisher?.trim()?.takeIf(String::isNotEmpty),
            publishedDate = volume.publishedDate?.trim()?.takeIf(String::isNotEmpty),
            language = volume.language?.trim()?.takeIf(String::isNotEmpty),
            pageCount = volume.pageCount?.takeIf { it > 0 },
            coverUrl = volume.thumbnailUrl?.replace("http://", "https://")
        )
    }

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
