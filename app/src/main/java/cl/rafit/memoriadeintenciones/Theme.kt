package cl.rafit.memoriadeintenciones

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppTheme(val label: String) {
    MADERA("Madera cálida"), BOSQUE("Bosque"), UNIVERSO("Universo"),
    PAPEL("Papel antiguo"), CELESTE("Celeste cielo"), MARINO("Azul marino")
}

private data class ThemePalette(
    val primary: Color, val onPrimary: Color, val primaryContainer: Color,
    val onPrimaryContainer: Color, val secondary: Color, val background: Color,
    val surface: Color, val onSurface: Color
)

private val palettes = mapOf(
    AppTheme.MADERA to ThemePalette(Color(0xFF7A4E2D), Color.White, Color(0xFFFFDCC2), Color(0xFF2B1608), Color(0xFF745944), Color(0xFFFFF8F3), Color(0xFFFFF8F3), Color(0xFF211A16)),
    AppTheme.BOSQUE to ThemePalette(Color(0xFF386A45), Color.White, Color(0xFFB9F0C2), Color(0xFF00210B), Color(0xFF526650), Color(0xFFF7FBF4), Color(0xFFF7FBF4), Color(0xFF181D18)),
    AppTheme.UNIVERSO to ThemePalette(Color(0xFF5B5D92), Color.White, Color(0xFFE1E0FF), Color(0xFF17174A), Color(0xFF5F5E72), Color(0xFFFAF8FF), Color(0xFFFAF8FF), Color(0xFF1B1B21)),
    AppTheme.PAPEL to ThemePalette(Color(0xFF765A2F), Color.White, Color(0xFFFFDEA6), Color(0xFF281900), Color(0xFF6C5D43), Color(0xFFFFF9EE), Color(0xFFFFF9EE), Color(0xFF201B13)),
    AppTheme.CELESTE to ThemePalette(Color(0xFF146C8E), Color.White, Color(0xFFB9EAFD), Color(0xFF001F2A), Color(0xFF4E626B), Color(0xFFF5FBFE), Color(0xFFF5FBFE), Color(0xFF171D1F)),
    AppTheme.MARINO to ThemePalette(Color(0xFF405A92), Color.White, Color(0xFFD8E2FF), Color(0xFF001A41), Color(0xFF59616F), Color(0xFFF9F9FF), Color(0xFFF9F9FF), Color(0xFF1A1B20))
)

@Composable
fun MemoriaTheme(theme: AppTheme, content: @Composable () -> Unit) {
    val palette = palettes.getValue(theme)
    val colors: ColorScheme = lightColorScheme(
        primary = palette.primary,
        onPrimary = palette.onPrimary,
        primaryContainer = palette.primaryContainer,
        onPrimaryContainer = palette.onPrimaryContainer,
        secondary = palette.secondary,
        background = palette.background,
        surface = palette.surface,
        onSurface = palette.onSurface
    )
    MaterialTheme(colorScheme = colors, content = content)
}
