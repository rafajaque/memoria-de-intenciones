package cl.rafit.memoriadeintenciones

import androidx.compose.ui.graphics.Color

data class LibraryCategory(
    val key: String,
    val title: String,
    val subtitle: String,
    val count: String,
    val symbol: String,
    val accent: Color
)

val libraryCategories = listOf(
    LibraryCategory("movies", "Películas", "Historias para ver", "0 títulos", "▣", Color(0xFFB8614C)),
    LibraryCategory("series", "Series", "Capítulos pendientes", "0 títulos", "▤", Color(0xFF467A70)),
    LibraryCategory("books", "Libros", "Lecturas por descubrir", "0 títulos", "▥", Color(0xFFB78945)),
    LibraryCategory("games", "Videojuegos", "Aventuras guardadas", "0 títulos", "◈", Color(0xFF6572A4))
)
