package cl.rafit.memoriadeintenciones

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Ajustes", style = MaterialTheme.typography.headlineMedium)
            Text("Elige el ambiente visual de tu biblioteca.", modifier = Modifier.padding(top = 8.dp, bottom = 12.dp))
        }
        items(AppTheme.entries) { theme ->
            Card(modifier = Modifier.fillMaxWidth().clickable { onThemeSelected(theme) }) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selectedTheme == theme, onClick = { onThemeSelected(theme) })
                    ColorSwatch(theme)
                    Text(theme.label, modifier = Modifier.padding(start = 12.dp))
                }
            }
        }
        item {
            Text("Los datos y preferencias se guardan localmente en este dispositivo.", modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.bodySmall)
            Text("Volver a la biblioteca", modifier = Modifier.padding(top = 12.dp).clickable(onClick = onBack), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun ColorSwatch(theme: AppTheme) {
    val color = when (theme) {
        AppTheme.MADERA -> Color(0xFF7A4E2D)
        AppTheme.BOSQUE -> Color(0xFF386A45)
        AppTheme.UNIVERSO -> Color(0xFF5B5D92)
        AppTheme.PAPEL -> Color(0xFF765A2F)
        AppTheme.CELESTE -> Color(0xFF146C8E)
        AppTheme.MARINO -> Color(0xFF405A92)
    }
    Box(modifier = Modifier.size(28.dp).background(color, MaterialTheme.shapes.small))
}
