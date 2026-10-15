package cl.rafit.memoriadeintenciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class LibraryCategory(val title: String, val subtitle: String)

private val demoCategories = listOf(
    LibraryCategory("Películas", "Marcadores de demostración"),
    LibraryCategory("Series", "Marcadores de demostración"),
    LibraryCategory("Libros", "Marcadores de demostración"),
    LibraryCategory("Videojuegos", "Marcadores de demostración")
)

@Composable
fun LibraryScreen(onOpenSettings: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Mi biblioteca", style = MaterialTheme.typography.headlineMedium)
                Text("Un espacio para tus próximas historias", style = MaterialTheme.typography.bodyLarge)
            }
            Button(onClick = onOpenSettings) { Text("Ajustes") }
        }
        Text("Contenido de demostración: todavía no hay datos reales.", style = MaterialTheme.typography.bodyMedium)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(demoCategories) { category ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(category.title, style = MaterialTheme.typography.titleMedium)
                        Text(category.subtitle, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
