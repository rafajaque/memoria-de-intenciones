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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.rafit.memoriadeintenciones.domain.LibraryCategory as StoredCategory
import cl.rafit.memoriadeintenciones.domain.LibraryStatus
import cl.rafit.memoriadeintenciones.ui.LibraryPaper
import kotlinx.coroutines.launch

@Composable
fun AddItemScreen(
    category: LibraryCategory,
    onSave: suspend (String, StoredCategory, LibraryStatus, Int?, String?) -> Result<Long>,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val materials = LocalLibraryMaterials.current
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(LibraryStatus.TO_CONSUME) }
    var duration by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var attemptedSave by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }
    val titleError = attemptedSave && title.isBlank()
    val parsedDuration = duration.toIntOrNull()
    val durationError = attemptedSave && duration.isNotBlank() && (parsedDuration == null || parsedDuration <= 0)

    LibraryPaper(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TextButton(onClick = onBack, enabled = !saving) { Text("←  Volver a ${category.title}") }
                HorizontalDivider(color = materials.ink.copy(alpha = .2f))
                Text(
                    "NUEVA HISTORIA",
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = materials.mutedInk
                )
                Text("Agregar título", style = MaterialTheme.typography.headlineLarge)
                Text(
                    "Quedará guardado en ${category.title} en este dispositivo.",
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = materials.mutedInk
                )
            }
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; formError = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Título") },
                    singleLine = true,
                    isError = titleError,
                    supportingText = if (titleError) ({ Text("Escribe un título para guardarlo.") }) else null
                )
            }
            item {
                Text("ESTADO", style = MaterialTheme.typography.labelSmall, color = materials.mutedInk)
                Column(
                    Modifier
                        .fillMaxWidth()
                        .selectableGroup()
                        .padding(top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LibraryStatus.entries.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(materials.paper, RoundedCornerShape(3.dp))
                                .border(1.dp, materials.ink.copy(alpha = .14f), RoundedCornerShape(3.dp))
                                .selectable(
                                    selected = status == option,
                                    role = Role.RadioButton,
                                    onClick = { status = option }
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = status == option, onClick = null)
                            Text(statusLabel(option), modifier = Modifier.padding(start = 8.dp), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = duration,
                    onValueChange = { value ->
                        duration = value.filter(Char::isDigit)
                        formError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Duración aproximada en minutos (opcional)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = durationError,
                    supportingText = if (durationError) ({ Text("Usa un número mayor que cero.") }) else null
                )
            }
            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it; formError = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nota personal (opcional)") },
                    minLines = 3,
                    maxLines = 5
                )
            }
            item {
                formError?.let { message ->
                    Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Button(
                    onClick = {
                        attemptedSave = true
                        if (title.isBlank() || (duration.isNotBlank() && (parsedDuration == null || parsedDuration <= 0))) {
                            return@Button
                        }
                        saving = true
                        scope.launch {
                            onSave(title, category.storedCategory, status, parsedDuration, notes)
                                .onSuccess { onSaved() }
                                .onFailure { error ->
                                    saving = false
                                    formError = error.message ?: "No se pudo guardar el título."
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !saving
                ) {
                    Text(if (saving) "Guardando…" else "Guardar en ${category.title}")
                }
            }
            item { androidx.compose.foundation.layout.Spacer(Modifier.size(4.dp)) }
        }
    }
}
