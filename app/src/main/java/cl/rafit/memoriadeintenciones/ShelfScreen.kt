package cl.rafit.memoriadeintenciones

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.rafit.memoriadeintenciones.domain.LibraryItem
import cl.rafit.memoriadeintenciones.domain.LibraryStatus
import cl.rafit.memoriadeintenciones.ui.BookplateMark
import cl.rafit.memoriadeintenciones.ui.CatalogueSlip
import cl.rafit.memoriadeintenciones.ui.LibraryPaper
import cl.rafit.memoriadeintenciones.ui.ShelfIllustration

@Composable
fun ShelfScreen(
    categoryKey: String,
    items: List<LibraryItem>,
    onAddItem: () -> Unit,
    onBack: () -> Unit
) {
    val category = libraryCategoryForKey(categoryKey)
    val index = libraryCategories.indexOfFirst { it.key == categoryKey }
    val materials = LocalLibraryMaterials.current
    LibraryPaper(Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                TextButton(onClick = onBack) { Text("←  Mi biblioteca") }
                HorizontalDivider(color = materials.ink.copy(alpha = .2f))
                Text(
                    if (index >= 0) "ESTANTERÍA 0${index + 1}" else "ESTANTERÍA",
                    Modifier.padding(top = 24.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = materials.mutedInk
                )
                Text(category?.title ?: "Estantería", Modifier.semantics { heading() }, style = MaterialTheme.typography.headlineLarge)
                Text(
                    category?.subtitle ?: "Historias por descubrir",
                    Modifier.padding(top = 6.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = materials.mutedInk
                )
                TextButton(
                    onClick = onAddItem,
                    enabled = category != null,
                    modifier = Modifier.padding(top = 10.dp)
                ) { Text("+  Agregar título") }
            }
            if (items.isEmpty()) {
                item { ShelfIllustration(categoryKey, Modifier.fillMaxWidth(), empty = true) }
                item {
                    CatalogueSlip(Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            BookplateMark(Modifier.size(36.dp))
                            Text("UN ESPACIO PARA TUS HISTORIAS", style = MaterialTheme.typography.labelSmall, color = materials.mutedInk)
                            Text("Tu primera historia\nempieza aquí.", style = MaterialTheme.typography.headlineMedium)
                            Text(
                                "Todavía no hay títulos en esta estantería. Aquí podrás reunir los que quieras recordar y disfrutar.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = materials.mutedInk
                            )
                            HorizontalDivider(color = materials.ink.copy(alpha = .15f))
                            Text("0 títulos guardados", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            } else {
                item { ShelfPreview(items, Modifier.fillMaxWidth()) }
                item {
                    Text(
                        "${items.size} ${if (items.size == 1) "título guardado" else "títulos guardados"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = materials.mutedInk
                    )
                }
                items(items, key = { it.id }) { item -> LibraryItemCard(item) }
            }
            item { Spacer(Modifier.size(4.dp)) }
        }
    }
}

@Composable
private fun LibraryItemCard(item: LibraryItem) {
    val materials = LocalLibraryMaterials.current
    CatalogueSlip(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(item.title, style = MaterialTheme.typography.titleLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(statusLabel(item.status), style = MaterialTheme.typography.labelSmall, color = materials.accent)
            item.durationMinutes?.let { minutes ->
                Text("Duración aproximada: $minutes min", style = MaterialTheme.typography.bodySmall, color = materials.mutedInk)
            }
            item.notes?.let { note ->
                Text(note, style = MaterialTheme.typography.bodySmall, color = materials.mutedInk)
            }
        }
    }
}

fun statusLabel(status: LibraryStatus): String = when (status) {
    LibraryStatus.TO_CONSUME -> "PENDIENTE"
    LibraryStatus.IN_PROGRESS -> "EN CURSO"
    LibraryStatus.COMPLETED -> "TERMINADO"
    LibraryStatus.ABANDONED -> "ABANDONADO"
}
