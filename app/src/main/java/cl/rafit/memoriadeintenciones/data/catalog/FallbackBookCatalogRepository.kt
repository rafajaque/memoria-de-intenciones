package cl.rafit.memoriadeintenciones.data.catalog

import cl.rafit.memoriadeintenciones.domain.BookCatalogProposal
import java.io.IOException
import retrofit2.HttpException

class FallbackBookCatalogRepository(
    private val primary: BookCatalogRepository,
    private val fallback: BookCatalogRepository
) : BookCatalogRepository {
    override suspend fun findByIsbn(isbn: String): List<BookCatalogProposal> {
        return try {
            primary.findByIsbn(isbn).ifEmpty { fallback.findByIsbn(isbn) }
        } catch (error: BookCatalogRateLimitedException) {
            fallback.findByIsbn(isbn)
        } catch (error: IOException) {
            fallback.findByIsbn(isbn)
        } catch (error: HttpException) {
            if (error.code() >= 500) fallback.findByIsbn(isbn) else throw error
        }
    }
}
