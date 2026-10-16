package cl.rafit.memoriadeintenciones.data.catalog

import cl.rafit.memoriadeintenciones.domain.BookCatalogProposal
import cl.rafit.memoriadeintenciones.domain.CatalogProvider
import cl.rafit.memoriadeintenciones.domain.Isbn

class OpenLibraryBookCatalogRepository(
    private val service: OpenLibraryService
) : BookCatalogRepository {
    override suspend fun findByIsbn(isbn: String): List<BookCatalogProposal> {
        val normalizedIsbn = Isbn.normalize(isbn)
        require(Isbn.isValid(normalizedIsbn)) { "El ISBN no es válido" }
        return service.searchByIsbn(normalizedIsbn).docs.mapNotNull { book ->
            val title = book.title?.trim()?.takeIf { it.isNotEmpty() } ?: return@mapNotNull null
            BookCatalogProposal(
                provider = CatalogProvider.OPEN_LIBRARY,
                externalId = book.key ?: "isbn:$normalizedIsbn",
                title = title,
                isbn = normalizedIsbn,
                authors = book.authors.map(String::trim).filter(String::isNotEmpty),
                publisher = book.publisher.firstOrNull()?.trim()?.takeIf(String::isNotEmpty),
                publishedDate = book.firstPublishYear?.toString(),
                language = book.language.firstOrNull()?.trim()?.takeIf(String::isNotEmpty),
                pageCount = book.pageCount?.takeIf { it > 0 },
                coverUrl = book.coverId?.let { "https://covers.openlibrary.org/b/id/${it}-M.jpg" }
            )
        }
    }
}
