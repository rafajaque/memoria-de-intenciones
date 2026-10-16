package cl.rafit.memoriadeintenciones.data.catalog

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class OpenLibraryBookCatalogRepositoryTest {
    @Test fun mapsBookMetadataByIsbn() = runBlocking {
        val service = object : OpenLibraryService {
            override suspend fun searchByIsbn(isbn: String, limit: Int) = OpenLibrarySearchResponse(
                listOf(OpenLibraryBook(
                    key = "/books/OL123M",
                    title = "Fuego y sangre",
                    authors = listOf("George R. R. Martin"),
                    publisher = listOf("Plaza & Janés"),
                    firstPublishYear = 2018,
                    language = listOf("spa"),
                    pageCount = 992,
                    coverId = 12345
                ))
            )
        }

        val proposal = OpenLibraryBookCatalogRepository(service).findByIsbn("978-84-663-7497-2").single()

        assertEquals("9788466374972", proposal.isbn)
        assertEquals("Fuego y sangre", proposal.title)
        assertEquals("https://covers.openlibrary.org/b/id/12345-M.jpg", proposal.coverUrl)
        assertTrue(proposal.authors.contains("George R. R. Martin"))
    }

    @Test fun fallbackUsesOpenLibraryAfterGoogleBooksRateLimit() = runBlocking {
        val primary = object : BookCatalogRepository {
            override suspend fun findByIsbn(isbn: String): List<cl.rafit.memoriadeintenciones.domain.BookCatalogProposal> {
                throw BookCatalogRateLimitedException()
            }
        }
        val fallback = object : BookCatalogRepository {
            override suspend fun findByIsbn(isbn: String) = listOf(
                cl.rafit.memoriadeintenciones.domain.BookCatalogProposal(
                    provider = cl.rafit.memoriadeintenciones.domain.CatalogProvider.OPEN_LIBRARY,
                    externalId = "/books/OL123M",
                    title = "Fuego y sangre",
                    isbn = isbn,
                    authors = emptyList(),
                    publisher = null,
                    publishedDate = null,
                    language = null,
                    pageCount = null,
                    coverUrl = null
                )
            )
        }

        val result = FallbackBookCatalogRepository(primary, fallback).findByIsbn("9788466374972")

        assertEquals("Fuego y sangre", result.single().title)
        assertEquals(cl.rafit.memoriadeintenciones.domain.CatalogProvider.OPEN_LIBRARY, result.single().provider)
    }
}
