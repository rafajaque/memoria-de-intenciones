package cl.rafit.memoriadeintenciones.data.shared

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

data class IncomingSharedImage(
    val uri: Uri,
    val declaredMimeType: String?
)

// MVP: adapta un URI de Share Sheet a un archivo que sólo existe en filesDir.
class SharedImageImporter(context: Context) {
    private val resolver: ContentResolver = context.contentResolver
    private val copier = PrivateImageCopier(File(context.filesDir, "shared-captures"))

    suspend fun import(incoming: IncomingSharedImage): SharedImageFile = withContext(Dispatchers.IO) {
        // Muchos emisores declaran image/*; el proveedor suele conocer el formato concreto.
        val mimeType = resolver.getType(incoming.uri) ?: incoming.declaredMimeType
        val stream = resolver.openInputStream(incoming.uri)
            ?: throw SharedImageImportException.PrivateStorageUnavailable
        copier.copy(mimeType, stream)
    }
}

// MVP: OCR con el modelo latino empaquetado; no transmite la imagen ni el texto.
class OnDeviceOcrTextReader(private val context: Context) {
    suspend fun read(imageFile: SharedImageFile): String = suspendCancellableCoroutine { continuation ->
        val input = runCatching { InputImage.fromFilePath(context, Uri.fromFile(imageFile.file)) }
            .getOrElse {
                continuation.resumeWithException(it)
                return@suspendCancellableCoroutine
            }
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            .process(input)
            .addOnSuccessListener { result ->
                if (continuation.isActive) continuation.resume(result.text)
            }
            .addOnFailureListener { error ->
                if (continuation.isActive) continuation.resumeWithException(error)
            }
    }
}
