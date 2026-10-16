package cl.rafit.memoriadeintenciones.data.catalog

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import okhttp3.MediaType.Companion.toMediaType

@Serializable data class GoogleBooksResponse(val items: List<GoogleBooksNetworkVolume> = emptyList())
@Serializable data class GoogleBooksNetworkVolume(val id: String, val volumeInfo: VolumeInfo = VolumeInfo())
@Serializable data class VolumeInfo(val title: String? = null, val authors: List<String> = emptyList(), val publisher: String? = null, val publishedDate: String? = null, val language: String? = null, val pageCount: Int? = null, val industryIdentifiers: List<BookIdentifier> = emptyList(), val imageLinks: ImageLinks? = null)
@Serializable data class ImageLinks(val thumbnail: String? = null)

interface GoogleBooksService {
    @GET("books/v1/volumes") suspend fun search(@Query("q") query: String, @Query("maxResults") maxResults: Int = 5): GoogleBooksResponse
    companion object {
        private val json = Json { ignoreUnknownKeys = true }
        fun create(): GoogleBooksService = Retrofit.Builder().baseUrl("https://www.googleapis.com/").addConverterFactory(json.asConverterFactory("application/json".toMediaType())).build().create(GoogleBooksService::class.java)
    }
}
