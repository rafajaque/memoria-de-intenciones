package cl.rafit.memoriadeintenciones

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import cl.rafit.memoriadeintenciones.ui.LibraryPaper
import cl.rafit.memoriadeintenciones.ui.ShelfIllustration

@Composable
fun SettingsScreen(
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    onBack: () -> Unit
) {
    val materials = LocalLibraryMaterials.current
    LibraryPaper(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().selectableGroup(),
            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TextButton(onClick = onBack) { Text("←  Mi biblioteca") }
                HorizontalDivider(color = materials.ink.copy(alpha = .2f))
                Text("AJUSTES", Modifier.padding(top = 24.dp, bottom = 8.dp), style = MaterialTheme.typography.labelSmall, color = materials.mutedInk)
                Text("El ambiente", Modifier.semantics { heading() }, style = MaterialTheme.typography.headlineLarge)
                Text(
                    "La misma biblioteca, otra luz.\nElige la madera y el papel de tu refugio.",
                    Modifier.padding(top = 8.dp, bottom = 12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = materials.mutedInk
                )
            }
            items(AppTheme.entries, key = { it.name }) { theme ->
                val selected = selectedTheme == theme
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(lerp(materials.paper, Color.White, .3f), RoundedCornerShape(3.dp))
                        .border(if (selected) 2.dp else 1.dp, if (selected) materials.accent else materials.ink.copy(alpha = .16f), RoundedCornerShape(3.dp))
                        .selectable(selected = selected, role = Role.RadioButton, onClick = { onThemeSelected(theme) })
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        ShelfIllustration("books", Modifier.width(80.dp), materials = libraryMaterials(theme))
                        Column(Modifier.weight(1f)) {
                            Text(theme.label, style = MaterialTheme.typography.titleMedium)
                            if (selected) Text("AMBIENTE ACTUAL", style = MaterialTheme.typography.labelSmall, color = materials.accent)
                        }
                        RadioButton(selected = selected, onClick = null)
                    }
                    Text(theme.description, style = MaterialTheme.typography.bodySmall, color = materials.mutedInk)
                }
            }
            item {
                Text(
                    "Tu elección se guarda en este dispositivo.",
                    Modifier.padding(vertical = 14.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = materials.mutedInk
                )
            }
        }
    }
}
