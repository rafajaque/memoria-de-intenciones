package cl.rafit.memoriadeintenciones.data.catalog

import cl.rafit.memoriadeintenciones.domain.BookCatalogProposal
import cl.rafit.memoriadeintenciones.domain.CatalogProvider
import cl.rafit.memoriadeintenciones.domain.Isbn
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

@Serializable
data class IsbnDbResponse(val book: IsbnDbBook? = null)

@Serializable
data class IsbnDbBook(
    val title: String? = null,
    val authors: List<String> = emptyList(),
    val publisher: String? = null,
    @SerialName("date_published") val publishedDate: String? = null,
    val language: String? = null,
    val pages: Int? = null,
    val image: String? = null
)

interface IsbnDbService {
    @GET("book/{isbn}")
    suspend fun findByIsbn(@Path("isbn") isbn: String, @Header("Authorization") apiKey: String): IsbnDbResponse

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun create(): IsbnDbService = Retrofit.Builder()
            .baseUrl("https://api2.isbndb.com/")
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(8, TimeUnit.SECONDS)
                    .readTimeout(8, TimeUnit.SECONDS)
                    .writeTimeout(8, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(IsbnDbService::class.java)
    }
}

class IsbnDbBookCatalogRepository(
    private val service: IsbnDbService,
    private val apiKey: String
) : BookCatalogRepository {
    override suspend fun findByIsbn(isbn: String): List<BookCatalogProposal> {
        val normalizedIsbn = Isbn.normalize(isbn)
        require(Isbn.isValid(normalizedIsbn)) { "El ISBN no es válido" }
        if (apiKey.isBlank()) return emptyList()
        val book = service.findByIsbn(normalizedIsbn, apiKey).book ?: return emptyList()
        val title = book.title?.trim()?.takeIf(String::isNotEmpty) ?: return emptyList()
        return listOf(
            BookCatalogProposal(
                provider = CatalogProvider.ISBNDB,
                externalId = "isbn:$normalizedIsbn",
                title = title,
                isbn = normalizedIsbn,
                authors = book.authors.map(String::trim).filter(String::isNotEmpty),
                publisher = book.publisher?.trim()?.takeIf(String::isNotEmpty),
                publishedDate = book.publishedDate?.trim()?.takeIf(String::isNotEmpty),
                language = book.language?.trim()?.takeIf(String::isNotEmpty),
                pageCount = book.pages?.takeIf { it > 0 },
                coverUrl = book.image?.trim()?.takeIf(String::isNotEmpty)
            )
        )
    }
}
