package cl.rafit.memoriadeintenciones

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AppTheme(val label: String, val description: String) {
    MADERA("Madera cálida", "Nogal, lino y detalles de latón"),
    BOSQUE("Bosque", "Madera verde y papel de herbario"),
    UNIVERSO("Universo", "Índigo, tinta y reflejos dorados"),
    PAPEL("Papel antiguo", "Roble claro y páginas de marfil"),
    CELESTE("Celeste cielo", "Madera azul y luz de mañana"),
    MARINO("Azul marino", "Azul profundo y encuadernaciones clásicas")
}

// MVP: materiales compartidos por la biblioteca y sus seis ambientes.
data class LibraryMaterials(
    val paper: Color,
    val ink: Color,
    val mutedInk: Color,
    val wood: Color,
    val woodLight: Color,
    val recess: Color,
    val brass: Color,
    val accent: Color
)

fun libraryMaterials(theme: AppTheme): LibraryMaterials = when (theme) {
    AppTheme.MADERA -> LibraryMaterials(Color(0xFFF3ECDD), Color(0xFF302B24), Color(0xFF716352), Color(0xFF795039), Color(0xFFB28A5E), Color(0xFF32261F), Color(0xFFD4B879), Color(0xFF674330))
    AppTheme.BOSQUE -> LibraryMaterials(Color(0xFFEEF0E5), Color(0xFF25382D), Color(0xFF596957), Color(0xFF365244), Color(0xFF6D8670), Color(0xFF1D3029), Color(0xFFD0BD83), Color(0xFF365442))
    AppTheme.UNIVERSO -> LibraryMaterials(Color(0xFFF0ECF5), Color(0xFF302E46), Color(0xFF676079), Color(0xFF44425C), Color(0xFF78748C), Color(0xFF202033), Color(0xFFD3BDA0), Color(0xFF514B75))
    AppTheme.PAPEL -> LibraryMaterials(Color(0xFFF7EED8), Color(0xFF3E3325), Color(0xFF74624B), Color(0xFFA17D50), Color(0xFFD0AC76), Color(0xFF51402C), Color(0xFFE4CD93), Color(0xFF71532C))
    AppTheme.CELESTE -> LibraryMaterials(Color(0xFFEDF4F4), Color(0xFF243D4B), Color(0xFF536D77), Color(0xFF5B8698), Color(0xFF99BFCC), Color(0xFF2D4C5C), Color(0xFFE0C894), Color(0xFF30647C))
    AppTheme.MARINO -> LibraryMaterials(Color(0xFFEBEEF2), Color(0xFF1F304B), Color(0xFF56657A), Color(0xFF243D60), Color(0xFF5A7596), Color(0xFF15253E), Color(0xFFD6BC84), Color(0xFF29476F))
}

val LocalLibraryMaterials = staticCompositionLocalOf { libraryMaterials(AppTheme.MADERA) }

private val editorialTypography = Typography(
    headlineLarge = TextStyle(fontFamily = FontFamily.Serif, fontSize = 38.sp, lineHeight = 44.sp, letterSpacing = (-1).sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Serif, fontSize = 32.sp, lineHeight = 38.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.Serif, fontSize = 25.sp, lineHeight = 30.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.Serif, fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp, lineHeight = 16.sp, letterSpacing = 1.5.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 1.sp)
)

@Composable
fun MemoriaTheme(theme: AppTheme, content: @Composable () -> Unit) {
    val materials = libraryMaterials(theme)
    val colors = lightColorScheme(
        primary = materials.accent,
        onPrimary = Color.White,
        primaryContainer = lerp(materials.paper, materials.woodLight, 0.25f),
        onPrimaryContainer = materials.ink,
        secondary = materials.mutedInk,
        onSecondary = Color.White,
        background = materials.paper,
        onBackground = materials.ink,
        surface = materials.paper,
        onSurface = materials.ink,
        surfaceVariant = lerp(materials.paper, materials.woodLight, 0.15f),
        onSurfaceVariant = materials.mutedInk,
        outline = materials.mutedInk,
        outlineVariant = materials.ink.copy(alpha = 0.18f)
    )
    CompositionLocalProvider(LocalLibraryMaterials provides materials) {
        MaterialTheme(
            colorScheme = colors,
            typography = editorialTypography,
            shapes = Shapes(small = RoundedCornerShape(4.dp), medium = RoundedCornerShape(6.dp), large = RoundedCornerShape(8.dp)),
            content = content
        )
    }
}
