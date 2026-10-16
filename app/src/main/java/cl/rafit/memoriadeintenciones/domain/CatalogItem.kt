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

enum class CatalogProvider { GOOGLE_BOOKS, OPEN_LIBRARY, ISBNDB, TMDB }

// MVP: propuesta remota editable; no equivale a un registro guardado en Room.
data class BookCatalogProposal(
    val provider: CatalogProvider,
    val externalId: String,
    val title: String,
    val isbn: String?,
    val authors: List<String>,
    val publisher: String?,
    val publishedDate: String?,
    val language: String?,
    val pageCount: Int?,
    val coverUrl: String?
)
