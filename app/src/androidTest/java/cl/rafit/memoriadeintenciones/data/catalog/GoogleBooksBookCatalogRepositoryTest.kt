package cl.rafit.memoriadeintenciones.data.catalog

import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class GoogleBooksBookCatalogRepositoryTest {
    @Test fun findsAndNormalizesBookProposalsWithoutNetwork() = runBlocking {
        val service = RecordingService(
            GoogleBooksResponse(
                listOf(
                    GoogleBooksNetworkVolume(
                        id = "volume-id",
                        volumeInfo = VolumeInfo(
                            title = "  Fuego y sangre ",
                            authors = listOf("George R. R. Martin"),
                            publisher = "Plaza & Janés",
                            publishedDate = "2018-11-20",
                            language = "es",
                            pageCount = 992,
                            industryIdentifiers = listOf(BookIdentifier("ISBN_13", "9780306406157")),
                            imageLinks = ImageLinks("http://example.com/cover.jpg")
                        )
                    )
                )
            )
        )
        val result = GoogleBooksBookCatalogRepository(service).findByIsbn("978-0-306-40615-7")

        assertEquals("isbn:9780306406157", service.query)
        assertEquals(5, service.maxResults)
        assertEquals("Fuego y sangre", result.single().title)
        assertEquals("https://example.com/cover.jpg", result.single().coverUrl)
    }

    @Test fun rejectsInvalidIsbnBeforeCallingService() = runBlocking {
        val service = RecordingService(GoogleBooksResponse())
        try {
            GoogleBooksBookCatalogRepository(service).findByIsbn("9780306406158")
            fail("Un ISBN inválido no debe llegar al servicio")
        } catch (_: IllegalArgumentException) {
            // Resultado esperado: el contrato corta antes de hacer red.
        }
        assertEquals(null, service.query)
    }

    @Test fun mapsRateLimitToRecoverableCatalogError() = runBlocking {
        val service = object : GoogleBooksService {
            override suspend fun search(query: String, maxResults: Int): GoogleBooksResponse =
                throw HttpException(Response.error<GoogleBooksResponse>(429, "{}".toResponseBody()))
        }

        try {
            GoogleBooksBookCatalogRepository(service).findByIsbn("9780306406157")
            fail("Un 429 debe convertirse en un error de límite reconocible")
        } catch (_: BookCatalogRateLimitedException) {
            // El formulario puede conservar el ISBN y continuar manualmente.
        }
    }

    private class RecordingService(private val response: GoogleBooksResponse) : GoogleBooksService {
        var query: String? = null
        var maxResults: Int? = null
        override suspend fun search(query: String, maxResults: Int): GoogleBooksResponse {
            this.query = query
            this.maxResults = maxResults
            return response
        }
    }
}
