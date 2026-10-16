package cl.rafit.memoriadeintenciones.domain

/** MVP: datos bibliográficos opcionales confirmados por la persona usuaria. */
data class BookMetadata(
    val isbn: String,
    val authors: List<String> = emptyList(),
    val publisher: String? = null,
    val publishedDate: String? = null,
    val language: String? = null,
    val pageCount: Int? = null,
    val coverUrl: String? = null,
    val edition: String? = null
)
