package cl.rafit.memoriadeintenciones.data.catalog

import cl.rafit.memoriadeintenciones.domain.BookCatalogProposal
import cl.rafit.memoriadeintenciones.domain.Isbn
import retrofit2.HttpException

class BookCatalogRateLimitedException : IllegalStateException()

interface BookCatalogRepository {
    suspend fun findByIsbn(isbn: String): List<BookCatalogProposal>
}

class GoogleBooksBookCatalogRepository(
    private val service: GoogleBooksService
) : BookCatalogRepository {
    override suspend fun findByIsbn(isbn: String): List<BookCatalogProposal> {
        val normalizedIsbn = Isbn.normalize(isbn)
        require(Isbn.isValid(normalizedIsbn)) { "El ISBN no es válido" }
        return try {
            service.search(query = "isbn:$normalizedIsbn", maxResults = 5)
        } catch (error: HttpException) {
            if (error.code() == 429) throw BookCatalogRateLimitedException()
            throw error
        }
            .items
            .map(GoogleBooksMapper::toVolume)
            .mapNotNull(GoogleBooksMapper::toBookProposal)
    }
}
