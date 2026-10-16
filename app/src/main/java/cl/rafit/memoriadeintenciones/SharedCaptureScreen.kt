package cl.rafit.memoriadeintenciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import cl.rafit.memoriadeintenciones.data.shared.IncomingSharedImage
import cl.rafit.memoriadeintenciones.data.shared.OnDeviceOcrTextReader
import cl.rafit.memoriadeintenciones.data.shared.SharedImageFile
import cl.rafit.memoriadeintenciones.data.shared.SharedImageImporter
import cl.rafit.memoriadeintenciones.ui.CatalogueSlip
import cl.rafit.memoriadeintenciones.ui.LibraryPaper

private sealed interface SharedCaptureState {
    data object Processing : SharedCaptureState
    data class Ready(val image: SharedImageFile, val text: String) : SharedCaptureState
    data class Failed(val message: String) : SharedCaptureState
}

@Composable
fun SharedCaptureScreen(
    incoming: IncomingSharedImage,
    importer: SharedImageImporter,
    ocrReader: OnDeviceOcrTextReader,
    onClose: () -> Unit
) {
    var state by remember(incoming) { mutableStateOf<SharedCaptureState>(SharedCaptureState.Processing) }
    var importedImage by remember(incoming) { mutableStateOf<SharedImageFile?>(null) }

    DisposableEffect(incoming) {
        onDispose { importedImage?.file?.delete() }
    }

    LaunchedEffect(incoming) {
        state = SharedCaptureState.Processing
        val result = runCatching {
            importer.import(incoming).also { importedImage = it }
        }.mapCatching { image ->
            ocrReader.read(image)
        }
        state = result.fold(
            onSuccess = { text -> SharedCaptureState.Ready(importedImage ?: error("Archivo no disponible"), text) },
            onFailure = { error ->
                importedImage?.file?.delete()
                importedImage = null
                SharedCaptureState.Failed(error.userFacingMessage())
            }
        )
    }

    fun closeAndDiscard() {
        importedImage?.file?.delete()
        importedImage = null
        onClose()
    }

    val materials = LocalLibraryMaterials.current
    LibraryPaper(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Text("CAPTURA COMPARTIDA", style = MaterialTheme.typography.labelSmall, color = materials.mutedInk)
                Text(
                    "Lectura local",
                    modifier = Modifier.padding(top = 8.dp).semantics { heading() },
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    "Esta prueba no crea un título ni envía la imagen fuera del dispositivo.",
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = materials.mutedInk
                )
            }
            item {
                CatalogueSlip(Modifier.fillMaxWidth()) {
                    when (val current = state) {
                        SharedCaptureState.Processing -> ProcessingContent()
                        is SharedCaptureState.Ready -> ReadyContent(current.text, ::closeAndDiscard)
                        is SharedCaptureState.Failed -> FailedContent(current.message, ::closeAndDiscard)
                    }
                }
            }
        }
    }
}

private fun Throwable.userFacingMessage(): String = when (this) {
    is cl.rafit.memoriadeintenciones.data.shared.SharedImageImportException.UnsupportedMimeType ->
        "Comparte una imagen JPEG, PNG o WebP."
    is cl.rafit.memoriadeintenciones.data.shared.SharedImageImportException.FileTooLarge ->
        "La captura supera el límite temporal de 15 MiB."
    else -> "No se pudo leer esta captura. Comprueba que la aplicación de origen permita compartirla."
}

@Composable
private fun ProcessingContent() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        CircularProgressIndicator()
        Text("Copiando la captura al espacio privado y leyendo su texto…", style = MaterialTheme.typography.titleMedium)
        Text("El archivo temporal se eliminará al cerrar esta ficha.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun ReadyContent(text: String, onClose: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("TEXTO DETECTADO", style = MaterialTheme.typography.labelSmall)
        Text(
            text.ifBlank { "No se detectó texto legible en esta captura." },
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            "Resultado temporal: aún no se guarda en tu biblioteca.",
            style = MaterialTheme.typography.bodySmall,
            fontStyle = FontStyle.Italic
        )
        Button(onClick = onClose) { Text("Cerrar y descartar") }
    }
}

@Composable
private fun FailedContent(message: String, onClose: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("NO SE PUDO LEER LA CAPTURA", style = MaterialTheme.typography.labelSmall)
        Text(message, style = MaterialTheme.typography.bodyMedium)
        TextButton(onClick = onClose) { Text("Volver a la biblioteca") }
    }
}
