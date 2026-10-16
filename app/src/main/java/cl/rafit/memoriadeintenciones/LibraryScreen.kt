package cl.rafit.memoriadeintenciones

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.rafit.memoriadeintenciones.domain.LibraryItem
import cl.rafit.memoriadeintenciones.ui.BookplateMark
import cl.rafit.memoriadeintenciones.ui.LibraryPaper
import cl.rafit.memoriadeintenciones.ui.LibraryUiState
import cl.rafit.memoriadeintenciones.ui.ShelfIllustration

@Composable
fun LibraryScreen(
    library: LibraryUiState,
    onOpenSettings: () -> Unit,
    onOpenRecommendations: () -> Unit,
    onOpenCategory: (LibraryCategory) -> Unit
) {
    val materials = LocalLibraryMaterials.current
    LibraryPaper(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 22.dp, end = 22.dp, top = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BookplateMark(Modifier.size(32.dp))
                    Text(
                        "MEMORIA\nDE INTENCIONES",
                        modifier = Modifier.weight(1f).padding(start = 10.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = materials.mutedInk
                    )
                    TextButton(onClick = onOpenSettings) { Text("Ajustes") }
                }
                HorizontalDivider(Modifier.padding(top = 10.dp), color = materials.ink.copy(alpha = .25f))
                Spacer(Modifier.height(22.dp))
                Text(
                    "Mi biblioteca",
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    "Historias que esperan su momento.",
                    modifier = Modifier.padding(top = 6.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = materials.mutedInk
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("TUS ESTANTERÍAS", style = MaterialTheme.typography.labelSmall)
                    Text("01 — 04", style = MaterialTheme.typography.labelSmall, color = materials.mutedInk)
                }
                Text(
                    if (library.items.isEmpty()) "Estantes ilustrados · Aún sin títulos" else "Tu colección, estantería por estantería",
                    modifier = Modifier.padding(top = 5.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = materials.mutedInk
                )
            }
            item {
                Button(onClick = onOpenRecommendations, modifier = Modifier.fillMaxWidth()) {
                    Text("Tengo tiempo libre")
                }
            }
            items(libraryCategories, key = { category -> category.key }) { category ->
                CategoryShelf(
                    category = category,
                    items = library.itemsFor(category.storedCategory),
                    onClick = { onOpenCategory(category) }
                )
            }
            item {
                HorizontalDivider(color = materials.ink.copy(alpha = .2f))
                Text(
                    "Cada historia tiene su lugar.",
                    modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontStyle = FontStyle.Italic,
                    color = materials.mutedInk
                )
            }
        }
    }
}

@Composable
private fun CategoryShelf(
    category: LibraryCategory,
    items: List<LibraryItem>,
    onClick: () -> Unit
) {
    val materials = LocalLibraryMaterials.current
    val shape = RoundedCornerShape(3.dp)
    val count = items.size
    Column(
        Modifier
            .fillMaxWidth()
            .shadow(3.dp, shape)
            .clip(shape)
            .background(materials.paper)
            .clickable(role = Role.Button, onClickLabel = "Abrir estantería de ${category.title}", onClick = onClick)
    ) {
        if (items.isEmpty()) {
            ShelfIllustration(category.key, Modifier.fillMaxWidth())
        } else {
            ShelfPreview(items, Modifier.fillMaxWidth())
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(lerp(materials.paper, Color.White, .28f))
                .border(BorderStroke(1.dp, materials.ink.copy(alpha = .14f)))
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(category.title, style = MaterialTheme.typography.titleLarge)
                Text("$count ${if (count == 1) "título" else "títulos"}", style = MaterialTheme.typography.bodySmall, color = materials.mutedInk)
            }
            Text("→", style = MaterialTheme.typography.titleLarge, color = materials.accent)
        }
    }
}

@Composable
fun ShelfPreview(items: List<LibraryItem>, modifier: Modifier = Modifier) {
    val materials = LocalLibraryMaterials.current
    val spineColors = listOf(materials.accent, materials.wood, materials.mutedInk, materials.woodLight)
    Box(modifier.aspectRatio(2.5f)) {
        ShelfIllustration("books", Modifier.fillMaxSize(), empty = true)
        Row(
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 28.dp, end = 30.dp, bottom = 44.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            items.take(6).forEachIndexed { index, item ->
                Box(
                    Modifier
                        .width(44.dp)
                        .height((94 + (index % 3) * 14).dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(spineColors[index % spineColors.size])
                        .border(1.dp, materials.brass.copy(alpha = .6f), RoundedCornerShape(2.dp))
                        .padding(5.dp)
                ) {
                    Text(
                        item.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
