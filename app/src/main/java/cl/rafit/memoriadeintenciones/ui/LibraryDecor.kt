package cl.rafit.memoriadeintenciones.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import cl.rafit.memoriadeintenciones.LibraryMaterials
import cl.rafit.memoriadeintenciones.LocalLibraryMaterials
import kotlin.math.sin

@Composable
fun LibraryPaper(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val materials = LocalLibraryMaterials.current
    Box(
        modifier.background(materials.paper).drawBehind {
            // Una trama estable y tenue; nunca cambia al recomponer.
            val step = 7.dp.toPx()
            var y = 0f
            while (y < size.height) {
                var x = 0f
                while (x < size.width) {
                    drawCircle(materials.ink.copy(alpha = 0.035f), 0.4.dp.toPx(), Offset(x, y))
                    x += step
                }
                y += step
            }
        },
        content = content
    )
}

@Composable
fun CatalogueSlip(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val materials = LocalLibraryMaterials.current
    Box(
        modifier
            .background(lerp(materials.paper, Color.White, 0.32f), RoundedCornerShape(2.dp))
            .border(1.dp, materials.ink.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
            .drawBehind {
                val x = 12.dp.toPx()
                drawLine(materials.accent.copy(alpha = 0.25f), Offset(x, 0f), Offset(x, size.height), 1.dp.toPx())
            }
            .padding(start = 28.dp, top = 22.dp, end = 22.dp, bottom = 22.dp),
        content = content
    )
}

@Composable
fun BookplateMark(modifier: Modifier = Modifier) {
    val ink = LocalLibraryMaterials.current.accent
    Canvas(modifier.clearAndSetSemantics { }) {
        val w = size.width
        val h = size.height
        val stroke = 1.5.dp.toPx()
        val book = Path().apply {
            moveTo(w * .5f, h * .25f)
            quadraticTo(w * .28f, h * .08f, w * .1f, h * .17f)
            lineTo(w * .1f, h * .72f)
            quadraticTo(w * .3f, h * .66f, w * .5f, h * .83f)
            quadraticTo(w * .7f, h * .66f, w * .9f, h * .72f)
            lineTo(w * .9f, h * .17f)
            quadraticTo(w * .7f, h * .08f, w * .5f, h * .25f)
            lineTo(w * .5f, h * .83f)
        }
        drawPath(book, ink, style = Stroke(stroke))
        for (i in 0..2) {
            val y = h * (.32f + .12f * i)
            drawLine(ink.copy(alpha = .55f), Offset(w * .2f, y), Offset(w * .4f, y + h * .06f), stroke * .65f)
            drawLine(ink.copy(alpha = .55f), Offset(w * .6f, y + h * .06f), Offset(w * .8f, y), stroke * .65f)
        }
    }
}

/**
 * Ilustración decorativa de la categoría, no una representación de registros.
 * Coordenadas normalizadas para mantener baldas y lomos proporcionales.
 */
@Composable
fun ShelfIllustration(
    categoryKey: String,
    modifier: Modifier = Modifier,
    empty: Boolean = false,
    materials: LibraryMaterials = LocalLibraryMaterials.current
) {
    Canvas(modifier.aspectRatio(2.5f).clearAndSetSemantics { }) {
        withTransform({ scale(size.width / 1000f, size.height / 400f, Offset.Zero) }) {
            drawRect(
                Brush.verticalGradient(listOf(materials.recess, lerp(materials.recess, materials.wood, .48f))),
                size = Size(1000f, 400f)
            )
            // Tablas del fondo, luz central y sombra bajo la cornisa.
            for (x in 0..1000 step 125) {
                drawLine(Color.Black.copy(alpha = .12f), Offset(x.toFloat(), 0f), Offset(x.toFloat(), 350f), 3f)
                drawLine(Color.White.copy(alpha = .035f), Offset(x + 4f, 0f), Offset(x + 4f, 350f), 2f)
            }
            drawRect(Brush.radialGradient(listOf(materials.woodLight.copy(alpha = .16f), Color.Transparent), Offset(480f, 220f), 550f), size = Size(1000f, 350f))
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = .32f), Color.Transparent), endY = 75f), size = Size(1000f, 80f))
            if (!empty) {
                when (categoryKey) {
                    "movies" -> {
                        spine(65f, 320f, 59f, 230f, Color(0xFF8B453A))
                        spine(126f, 320f, 49f, 242f, Color(0xFFB79B6F))
                        spine(177f, 320f, 57f, 214f, Color(0xFF435F5C))
                        rotate(-8f, Offset(270f, 320f)) { spine(248f, 320f, 50f, 242f, Color(0xFF384752)) }
                        cover(357f, 320f, 203f, 269f, Color(0xFF85483F), categoryKey)
                        stackedVolume(643f, 320f, 255f, 37f, Color(0xFF465B57))
                        stackedVolume(653f, 280f, 234f, 34f, Color(0xFFA38A61))
                    }
                    "series" -> {
                        for (i in 0..4) spine(68f + i * 56f, 320f, 52f, 224f + (i % 2) * 10f, lerp(Color(0xFF354F4C), Color(0xFF748476), i / 7f))
                        cover(419f, 320f, 197f, 266f, Color(0xFF3A625C), categoryKey)
                        spine(715f, 320f, 69f, 251f, Color(0xFFAB906A))
                        spine(787f, 320f, 58f, 218f, Color(0xFF754C40))
                        rotate(9f, Offset(856f, 320f)) { spine(850f, 320f, 48f, 228f, Color(0xFF4E555E)) }
                    }
                    "books" -> {
                        stackedVolume(65f, 320f, 235f, 45f, Color(0xFF754D43))
                        stackedVolume(75f, 272f, 224f, 39f, Color(0xFF6D7359))
                        stackedVolume(67f, 230f, 210f, 35f, Color(0xFFAE8A56))
                        cover(372f, 320f, 204f, 268f, Color(0xFFB18A51), categoryKey)
                        spine(676f, 320f, 63f, 249f, Color(0xFF435A58))
                        spine(742f, 320f, 72f, 231f, Color(0xFF875143))
                        rotate(7f, Offset(849f, 320f)) { spine(832f, 320f, 57f, 257f, Color(0xFFB4A080)) }
                    }
                    else -> {
                        spine(82f, 320f, 57f, 237f, Color(0xFF5A607B))
                        spine(142f, 320f, 51f, 237f, Color(0xFF8B7357))
                        spine(196f, 320f, 55f, 237f, Color(0xFF4C6867))
                        cover(350f, 320f, 205f, 262f, Color(0xFF56617C), categoryKey)
                        stackedVolume(653f, 320f, 248f, 34f, Color(0xFF865846))
                        stackedVolume(663f, 283f, 228f, 31f, Color(0xFF40535B))
                    }
                }
            } else {
                // Sujetalibros vacío: la falta de contenido es real.
                drawRect(Color.Black.copy(alpha = .14f), Offset(405f, 308f), Size(220f, 15f))
                drawRect(materials.brass.copy(alpha = .6f), Offset(449f, 150f), Size(8f, 170f))
                drawRect(materials.brass.copy(alpha = .6f), Offset(449f, 312f), Size(130f, 8f))
            }
            drawRect(materials.wood, Offset(0f, 0f), Size(16f, 350f))
            drawRect(materials.wood, Offset(984f, 0f), Size(16f, 350f))
            plank(materials, 320f)
        }
    }
}

private fun DrawScope.plank(materials: LibraryMaterials, top: Float) {
    drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = .4f), Color.Transparent), startY = top + 38, endY = 400f), Offset(0f, top + 38), Size(1000f, 42f))
    val lip = Path().apply {
        moveTo(16f, top); lineTo(984f, top); lineTo(1000f, top + 16f); lineTo(0f, top + 16f); close()
    }
    drawPath(lip, materials.woodLight)
    drawRect(Brush.verticalGradient(listOf(materials.woodLight, materials.wood), startY = top + 16f, endY = top + 50f), Offset(0f, top + 16f), Size(1000f, 34f))
    for (row in 0..5) {
        val grain = Path().apply {
            moveTo(0f, top + 20f + row * 5f)
            for (x in 0..1000 step 20) lineTo(x.toFloat(), top + 20f + row * 5f + sin(x * .011f + row) * 2.5f)
        }
        drawPath(grain, materials.recess.copy(alpha = .2f), style = Stroke(1f))
    }
    drawLine(materials.brass.copy(alpha = .38f), Offset(0f, top + 17f), Offset(1000f, top + 17f), 2f)
}

private fun DrawScope.spine(x: Float, base: Float, width: Float, height: Float, color: Color) {
    val top = base - height
    drawRect(Color.Black.copy(alpha = .22f), Offset(x + 7f, top + 6f), Size(width, height - 5f))
    drawRoundRect(Brush.horizontalGradient(listOf(lerp(color, Color.Black, .28f), color, lerp(color, Color.White, .12f), color), startX = x, endX = x + width), Offset(x, top), Size(width, height), CornerRadius(5f))
    drawLine(Color.White.copy(alpha = .18f), Offset(x + 6, top + 3), Offset(x + 6, base - 3), 1.5f)
    val gold = Color(0xFFE1C78E).copy(alpha = .65f)
    for (y in listOf(top + 24f, top + 30f, base - 27f, base - 21f)) {
        drawLine(gold, Offset(x + 8f, y), Offset(x + width - 8f, y), 2f)
    }
    drawRect(gold.copy(alpha = .25f), Offset(x + width * .38f, top + 62f), Size(width * .24f, height * .35f))
}

private fun DrawScope.stackedVolume(x: Float, base: Float, width: Float, height: Float, color: Color) {
    drawRect(Color.Black.copy(alpha = .25f), Offset(x + 8f, base - height + 6f), Size(width, height))
    drawRoundRect(color, Offset(x, base - height), Size(width, height), CornerRadius(3f))
    drawRect(Color(0xFFD7C6A4), Offset(x + 8f, base - height + 5f), Size(width - 10, height - 10))
    for (i in 1..4) {
        val y = base - height + 5f + (height - 10f) * i / 5f
        drawLine(Color(0xFF857153).copy(alpha = .45f), Offset(x + 12, y), Offset(x + width - 6, y), 1f)
    }
    drawRect(color, Offset(x, base - height), Size(14f, height))
}

private fun DrawScope.cover(x: Float, base: Float, width: Float, height: Float, color: Color, category: String) {
    val top = base - height
    drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = .35f), Color.Transparent), startX = x + width, endX = x + width + 24f), Offset(x + width, top + 8), Size(24f, height - 8))
    drawRoundRect(Color(0xFFD6C6A8), Offset(x + 8f, top - 3f), Size(width - 5, height), CornerRadius(3f))
    drawRoundRect(Brush.horizontalGradient(listOf(lerp(color, Color.Black, .28f), color, lerp(color, Color.White, .06f)), startX = x, endX = x + width), Offset(x, top), Size(width, height), CornerRadius(4f))
    drawLine(Color.Black.copy(alpha = .22f), Offset(x + 17, top), Offset(x + 17, base), 2.5f)
    for (line in 0..38) {
        val y = top + line * height / 39f
        drawLine(Color.White.copy(alpha = .04f), Offset(x + 20, y), Offset(x + width - 2, y), 1f)
    }
    val gold = Color(0xFFE6D3A2)
    drawRect(gold.copy(alpha = .5f), Offset(x + 30, top + 24), Size(width - 48, height - 48), style = Stroke(1.5f))
    val cx = x + width * .54f
    val cy = top + height * .45f
    drawCircle(gold.copy(alpha = .6f), 36f, Offset(cx, cy), style = Stroke(1.5f))
    when (category) {
        "movies" -> {
            drawRect(gold, Offset(cx - 19, cy - 15), Size(38f, 30f), style = Stroke(2f))
            for (i in 0..3) {
                drawRect(gold, Offset(cx - 16 + i * 9f, cy - 12), Size(4f, 4f))
                drawRect(gold, Offset(cx - 16 + i * 9f, cy + 8), Size(4f, 4f))
            }
        }
        "series" -> {
            for (i in 0..2) drawRect(gold.copy(alpha = 1f - i * .18f), Offset(cx - 21 + i * 6, cy - 17 + i * 6), Size(30f, 24f), style = Stroke(2f))
        }
        "books" -> {
            drawLine(gold, Offset(cx, cy - 17), Offset(cx, cy + 19), 2f)
            drawRect(gold, Offset(cx - 22, cy - 15), Size(44f, 30f), style = Stroke(2f))
            drawLine(gold, Offset(cx - 17, cy - 5), Offset(cx - 5, cy - 5), 1.5f)
            drawLine(gold, Offset(cx + 5, cy - 5), Offset(cx + 17, cy - 5), 1.5f)
        }
        else -> {
            drawLine(gold, Offset(cx - 24, cy), Offset(cx - 4, cy), 4f)
            drawLine(gold, Offset(cx - 14, cy - 10), Offset(cx - 14, cy + 10), 4f)
            drawCircle(gold, 4f, Offset(cx + 14, cy - 6))
            drawCircle(gold, 4f, Offset(cx + 22, cy + 5))
        }
    }
    for (i in 0..2) {
        val half = if (i == 1) width * .22f else width * .15f
        drawLine(gold.copy(alpha = .7f), Offset(cx - half, top + height * .72f + i * 9), Offset(cx + half, top + height * .72f + i * 9), 2f)
    }
}
