package cl.rafit.memoriadeintenciones.data.catalog

import cl.rafit.memoriadeintenciones.domain.BookCatalogProposal
import cl.rafit.memoriadeintenciones.domain.Isbn

interface BookCatalogRepository {
    suspend fun findByIsbn(isbn: String): List<BookCatalogProposal>
}

class GoogleBooksBookCatalogRepository(
    private val service: GoogleBooksService
) : BookCatalogRepository {
    override suspend fun findByIsbn(isbn: String): List<BookCatalogProposal> {
        val normalizedIsbn = Isbn.normalize(isbn)
        require(Isbn.isValid(normalizedIsbn)) { "El ISBN no es válido" }
        return service.search(query = "isbn:$normalizedIsbn", maxResults = 5)
            .items
            .map(GoogleBooksMapper::toVolume)
            .mapNotNull(GoogleBooksMapper::toBookProposal)
    }
}
