package cl.rafit.memoriadeintenciones.data.catalog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@Serializable
data class OpenLibrarySearchResponse(val docs: List<OpenLibraryBook> = emptyList())

@Serializable
data class OpenLibraryBook(
    val key: String? = null,
    val title: String? = null,
    @SerialName("author_name") val authors: List<String> = emptyList(),
    val publisher: List<String> = emptyList(),
    @SerialName("first_publish_year") val firstPublishYear: Int? = null,
    val language: List<String> = emptyList(),
    @SerialName("number_of_pages_median") val pageCount: Int? = null,
    @SerialName("cover_i") val coverId: Int? = null
)

interface OpenLibraryService {
    @GET("search.json")
    suspend fun searchByIsbn(@Query("isbn") isbn: String, @Query("limit") limit: Int = 1): OpenLibrarySearchResponse

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun create(): OpenLibraryService = Retrofit.Builder()
            .baseUrl("https://openlibrary.org/")
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(8, TimeUnit.SECONDS)
                    .readTimeout(8, TimeUnit.SECONDS)
                    .writeTimeout(8, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(OpenLibraryService::class.java)
    }
}
