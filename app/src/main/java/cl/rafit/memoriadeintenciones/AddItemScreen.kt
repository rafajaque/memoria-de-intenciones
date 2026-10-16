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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
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
import cl.rafit.memoriadeintenciones.domain.LibraryItem
import cl.rafit.memoriadeintenciones.domain.LibraryStatus
import cl.rafit.memoriadeintenciones.domain.BookCatalogProposal
import cl.rafit.memoriadeintenciones.domain.BookMetadata
import cl.rafit.memoriadeintenciones.data.catalog.BookCatalogRateLimitedException
import cl.rafit.memoriadeintenciones.ui.LibraryPaper
import cl.rafit.memoriadeintenciones.ui.BookCover
import kotlinx.coroutines.launch

@Composable
fun AddItemScreen(
    category: LibraryCategory,
    existingItem: LibraryItem? = null,
    onSave: suspend (String, StoredCategory, LibraryStatus, Int?, String?, BookMetadata?) -> Result<Long>,
    onUpdate: suspend (LibraryItem, String, LibraryStatus, Int?, String?, BookMetadata?) -> Result<Unit>,
    onDelete: suspend (Long) -> Result<Unit>,
    scannedIsbn: String? = null,
    onScanIsbn: (() -> Unit)? = null,
    onFindBook: (suspend (String) -> Result<List<BookCatalogProposal>>)? = null,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val materials = LocalLibraryMaterials.current
    val scope = rememberCoroutineScope()
    val editing = existingItem != null
    var title by remember(existingItem?.id) { mutableStateOf(existingItem?.title.orEmpty()) }
    var status by remember(existingItem?.id) { mutableStateOf(existingItem?.status ?: LibraryStatus.TO_CONSUME) }
    var duration by remember(existingItem?.id) { mutableStateOf(existingItem?.durationMinutes?.toString().orEmpty()) }
    var notes by remember(existingItem?.id) { mutableStateOf(existingItem?.notes.orEmpty()) }
    var isbn by remember(existingItem?.id) { mutableStateOf(existingItem?.bookMetadata?.isbn.orEmpty()) }
    var authors by remember(existingItem?.id) { mutableStateOf(existingItem?.bookMetadata?.authors?.joinToString(", ").orEmpty()) }
    var publisher by remember(existingItem?.id) { mutableStateOf(existingItem?.bookMetadata?.publisher.orEmpty()) }
    var publishedDate by remember(existingItem?.id) { mutableStateOf(existingItem?.bookMetadata?.publishedDate.orEmpty()) }
    var language by remember(existingItem?.id) { mutableStateOf(existingItem?.bookMetadata?.language.orEmpty()) }
    var pageCount by remember(existingItem?.id) { mutableStateOf(existingItem?.bookMetadata?.pageCount?.toString().orEmpty()) }
    var coverUrl by remember(existingItem?.id) { mutableStateOf(existingItem?.bookMetadata?.coverUrl.orEmpty()) }
    var catalogProposals by remember { mutableStateOf<List<BookCatalogProposal>>(emptyList()) }
    var catalogError by remember { mutableStateOf<String?>(null) }
    var catalogLoading by remember { mutableStateOf(false) }
    var searchedIsbn by remember { mutableStateOf<String?>(null) }
    var attemptedSave by remember(existingItem?.id) { mutableStateOf(false) }
    var saving by remember(existingItem?.id) { mutableStateOf(false) }
    var formError by remember(existingItem?.id) { mutableStateOf<String?>(null) }
    var showDeleteConfirmation by remember(existingItem?.id) { mutableStateOf(false) }
    val titleError = attemptedSave && title.isBlank()
    val parsedDuration = duration.toIntOrNull()
    val durationError = attemptedSave && duration.isNotBlank() && (parsedDuration == null || parsedDuration <= 0)
    val parsedPageCount = pageCount.toIntOrNull()
    val bookMetadata = isbn.trim().takeIf { it.isNotEmpty() }?.let {
        BookMetadata(
            isbn = it,
            authors = authors.split(",").map(String::trim).filter(String::isNotEmpty),
            publisher = publisher.trim().takeIf(String::isNotEmpty),
            publishedDate = publishedDate.trim().takeIf(String::isNotEmpty),
            language = language.trim().takeIf(String::isNotEmpty),
            pageCount = parsedPageCount?.takeIf { count -> count > 0 },
            coverUrl = coverUrl.trim().takeIf(String::isNotEmpty),
            coverLocalPath = existingItem?.bookMetadata
                ?.takeIf { it.coverUrl == coverUrl.trim().takeIf(String::isNotEmpty) }
                ?.coverLocalPath
        )
    }

    fun applyProposal(proposal: BookCatalogProposal) {
        title = proposal.title
        isbn = proposal.isbn ?: isbn
        authors = proposal.authors.joinToString(", ")
        publisher = proposal.publisher.orEmpty()
        publishedDate = proposal.publishedDate.orEmpty()
        language = proposal.language.orEmpty()
        pageCount = proposal.pageCount?.toString().orEmpty()
        coverUrl = proposal.coverUrl.orEmpty()
        catalogProposals = emptyList()
    }

    LaunchedEffect(scannedIsbn, onFindBook) {
        val isbnToSearch = scannedIsbn?.takeIf { it != searchedIsbn } ?: return@LaunchedEffect
        searchedIsbn = isbnToSearch
        isbn = isbnToSearch
        if (onFindBook == null) return@LaunchedEffect
        catalogLoading = true
        catalogError = null
        onFindBook(isbnToSearch)
            .onSuccess { proposals ->
                catalogProposals = proposals
                if (proposals.size == 1) applyProposal(proposals.single())
                if (proposals.isEmpty()) catalogError = "No encontramos este ISBN en nuestros catálogos. Completa los datos manualmente."
            }
            .onFailure { error ->
                catalogError = when (error) {
                    is BookCatalogRateLimitedException -> "Google Books está temporalmente limitado. El ISBN quedó cargado; completa el título manualmente o inténtalo más tarde."
                    else -> error.message ?: "No se pudo consultar Google Books. Puedes completar el formulario manualmente."
                }
            }
        catalogLoading = false
    }

    if (showDeleteConfirmation && existingItem != null) {
        AlertDialog(
            onDismissRequest = { if (!saving) showDeleteConfirmation = false },
            title = { Text("¿Eliminar título?") },
            text = { Text("“${existingItem.title}” se quitará de ${category.title}. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    enabled = !saving,
                    onClick = {
                        saving = true
                        scope.launch {
                            onDelete(existingItem.id)
                                .onSuccess { onSaved() }
                                .onFailure { error ->
                                    saving = false
                                    showDeleteConfirmation = false
                                    formError = error.message ?: "No se pudo eliminar el título."
                                }
                        }
                    }
                ) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }, enabled = !saving) { Text("Cancelar") }
            }
        )
    }

    if (catalogProposals.size > 1) {
        AlertDialog(
            onDismissRequest = { catalogProposals = emptyList() },
            title = { Text("Elige una ficha") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    catalogProposals.forEach { proposal ->
                        TextButton(onClick = { applyProposal(proposal) }, modifier = Modifier.fillMaxWidth()) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                BookCover(proposal.coverUrl, proposal.title, Modifier.size(48.dp, 72.dp))
                                Text(proposal.title + proposal.authors.firstOrNull()?.let { "\n$it" }.orEmpty())
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { catalogProposals = emptyList() }) { Text("Completar manualmente") } }
        )
    }

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
                    if (editing) "FICHA DE LA HISTORIA" else "NUEVA HISTORIA",
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = materials.mutedInk
                )
                Text(if (editing) "Editar título" else "Agregar título", style = MaterialTheme.typography.headlineLarge)
                Text(
                    if (editing) "Actualizarás la ficha en ${category.title}." else "Quedará guardado en ${category.title} en este dispositivo.",
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = materials.mutedInk
                )
            }
            item {
                if (onScanIsbn != null) {
                    TextButton(onClick = onScanIsbn) { Text("Escanear ISBN") }
                    scannedIsbn?.let { Text(if (catalogLoading) "Consultando Google Books…" else "ISBN leído: $it", style = MaterialTheme.typography.bodySmall) }
                }
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
            if (category.storedCategory == StoredCategory.BOOK && (isbn.isNotBlank() || catalogError != null)) {
                item {
                    Text("DATOS BIBLIOGRÁFICOS", style = MaterialTheme.typography.labelSmall, color = materials.mutedInk)
                    catalogError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                    OutlinedTextField(isbn, { isbn = it }, Modifier.fillMaxWidth(), label = { Text("ISBN") }, singleLine = true)
                    OutlinedTextField(authors, { authors = it }, Modifier.fillMaxWidth(), label = { Text("Autores (separados por coma)") }, singleLine = true)
                    OutlinedTextField(publisher, { publisher = it }, Modifier.fillMaxWidth(), label = { Text("Editorial") }, singleLine = true)
                    OutlinedTextField(publishedDate, { publishedDate = it }, Modifier.fillMaxWidth(), label = { Text("Fecha de publicación") }, singleLine = true)
                    OutlinedTextField(language, { language = it }, Modifier.fillMaxWidth(), label = { Text("Idioma") }, singleLine = true)
                    OutlinedTextField(pageCount, { pageCount = it.filter(Char::isDigit) }, Modifier.fillMaxWidth(), label = { Text("Páginas") }, singleLine = true)
                    if (coverUrl.isNotBlank()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            BookCover(coverUrl, title.ifBlank { "libro" }, Modifier.size(72.dp, 108.dp), existingItem?.bookMetadata?.coverLocalPath)
                            Text("La portada se guardará en el almacenamiento privado al confirmar.", style = MaterialTheme.typography.bodySmall, color = materials.mutedInk)
                        }
                    }
                }
            }
            item {
                Text("ESTADO", style = MaterialTheme.typography.labelSmall, color = materials.mutedInk)
                Column(
                    Modifier.fillMaxWidth().selectableGroup().padding(top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LibraryStatus.entries.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(materials.paper, RoundedCornerShape(3.dp))
                                .border(1.dp, materials.ink.copy(alpha = .14f), RoundedCornerShape(3.dp))
                                .selectable(status == option, role = Role.RadioButton) { status = option }
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
                    onValueChange = { value -> duration = value.filter(Char::isDigit); formError = null },
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
                formError?.let { message -> Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                Button(
                    onClick = {
                        attemptedSave = true
                        if (title.isBlank() || (duration.isNotBlank() && (parsedDuration == null || parsedDuration <= 0))) return@Button
                        saving = true
                        scope.launch {
                            val result = if (existingItem == null) {
                                onSave(title, category.storedCategory, status, parsedDuration, notes, bookMetadata).map { }
                            } else {
                                onUpdate(existingItem, title, status, parsedDuration, notes, bookMetadata)
                            }
                            result.onSuccess { onSaved() }.onFailure { error ->
                                saving = false
                                formError = error.message ?: "No se pudo guardar el título."
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !saving
                ) { Text(if (saving) "Guardando…" else if (editing) "Guardar cambios" else "Guardar en ${category.title}") }
                if (editing) {
                    TextButton(
                        onClick = { showDeleteConfirmation = true },
                        enabled = !saving,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) { Text("Eliminar título", color = MaterialTheme.colorScheme.error) }
                }
            }
            item { androidx.compose.foundation.layout.Spacer(Modifier.size(4.dp)) }
        }
    }
}
