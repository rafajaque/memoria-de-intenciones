package cl.rafit.memoriadeintenciones

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.rafit.memoriadeintenciones.domain.Recommendation
import cl.rafit.memoriadeintenciones.ui.LibraryPaper
import cl.rafit.memoriadeintenciones.ui.RecommendationUiState

@Composable
fun RecommendationScreen(
    state: RecommendationUiState,
    onMinutesChanged: (String) -> Unit,
    onAccept: (Recommendation) -> Unit,
    onPostpone: (Recommendation) -> Unit,
    onDismiss: (Recommendation) -> Unit,
    onRealize: (Recommendation) -> Unit,
    onBack: () -> Unit
) {
    val materials = LocalLibraryMaterials.current
    LibraryPaper(Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                TextButton(onClick = onBack) { Text("←  Mi biblioteca") }
                HorizontalDivider(color = materials.ink.copy(alpha = .2f))
                Text(
                    "TENGO TIEMPO LIBRE",
                    modifier = Modifier.padding(top = 24.dp).semantics { heading() },
                    style = MaterialTheme.typography.labelSmall,
                    color = materials.mutedInk
                )
                Text("Elige qué cabe hoy", style = MaterialTheme.typography.headlineLarge)
                Text(
                    "Recomendaciones calculadas sólo con tu biblioteca local.",
                    modifier = Modifier.padding(top = 6.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = materials.mutedInk
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = state.inputMinutes,
                        onValueChange = onMinutesChanged,
                        modifier = Modifier.weight(1f),
                        label = { Text("Minutos disponibles") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = state.error != null,
                        supportingText = state.error?.let { message -> ({ Text(message) }) }
                    )
                }
            }
            if (state.inputMinutes.isNotBlank() && state.error == null && state.recommendations.isEmpty()) {
                item {
                    Text(
                        "No hay títulos pendientes disponibles para este momento.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = materials.mutedInk
                    )
                }
            }
            items(state.recommendations, key = { it.item.id }) { recommendation ->
                RecommendationCard(
                    recommendation = recommendation,
                    onAccept = { onAccept(recommendation) },
                    onPostpone = { onPostpone(recommendation) },
                    onDismiss = { onDismiss(recommendation) },
                    onRealize = { onRealize(recommendation) }
                )
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    recommendation: Recommendation,
    onAccept: () -> Unit,
    onPostpone: () -> Unit,
    onDismiss: () -> Unit,
    onRealize: () -> Unit
) {
    val materials = LocalLibraryMaterials.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, materials.ink.copy(alpha = .18f), RoundedCornerShape(4.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Text(recommendation.item.title, style = MaterialTheme.typography.titleLarge)
        Text(
            "${categoryLabel(recommendation.item.category)} · ${recommendation.item.durationMinutes?.let { "$it min" } ?: "duración no indicada"}",
            style = MaterialTheme.typography.labelMedium,
            color = materials.mutedInk
        )
        recommendation.reasons.forEach { reason ->
            Text("• ${reason.text}", style = MaterialTheme.typography.bodySmall, color = materials.mutedInk)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onRealize, modifier = Modifier.weight(1f)) { Text("Realizar") }
            OutlinedButton(onClick = onAccept) { Text("Aceptar") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = onPostpone, modifier = Modifier.weight(1f)) { Text("Posponer") }
            TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Descartar") }
        }
    }
}

private fun categoryLabel(category: cl.rafit.memoriadeintenciones.domain.LibraryCategory): String = when (category) {
    cl.rafit.memoriadeintenciones.domain.LibraryCategory.MOVIE -> "Película"
    cl.rafit.memoriadeintenciones.domain.LibraryCategory.SERIES -> "Serie"
    cl.rafit.memoriadeintenciones.domain.LibraryCategory.BOOK -> "Libro"
    cl.rafit.memoriadeintenciones.domain.LibraryCategory.GAME -> "Videojuego"
}
