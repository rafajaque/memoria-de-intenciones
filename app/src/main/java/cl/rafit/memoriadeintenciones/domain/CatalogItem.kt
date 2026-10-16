package cl.rafit.memoriadeintenciones.domain

// MVP: contrato normalizado, sin red ni persistencia todavía.
data class CatalogItem(
    val provider: CatalogProvider,
    val externalId: String,
    val title: String,
    val contributors: List<String>,
    val publisher: String?,
    val publishedYear: Int?,
    val isbn: String?,
    val thumbnailUrl: String?
)

enum class CatalogProvider { GOOGLE_BOOKS, OPEN_LIBRARY, TMDB }
