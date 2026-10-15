package cl.rafit.memoriadeintenciones

import androidx.compose.ui.graphics.Color
import cl.rafit.memoriadeintenciones.domain.LibraryCategory as StoredCategory

data class LibraryCategory(
    val key: String,
    val title: String,
    val subtitle: String,
    val storedCategory: StoredCategory,
    val symbol: String,
    val accent: Color
)

val libraryCategories = listOf(
    LibraryCategory("movies", "Películas", "Historias para ver", StoredCategory.MOVIE, "▣", Color(0xFFB8614C)),
    LibraryCategory("series", "Series", "Capítulos pendientes", StoredCategory.SERIES, "▤", Color(0xFF467A70)),
    LibraryCategory("books", "Libros", "Lecturas por descubrir", StoredCategory.BOOK, "▥", Color(0xFFB78945)),
    LibraryCategory("games", "Videojuegos", "Aventuras guardadas", StoredCategory.GAME, "◈", Color(0xFF6572A4))
)

fun libraryCategoryForKey(key: String): LibraryCategory? =
    libraryCategories.firstOrNull { it.key == key }
