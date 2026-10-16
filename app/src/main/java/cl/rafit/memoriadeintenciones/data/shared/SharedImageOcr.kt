package cl.rafit.memoriadeintenciones.data.shared

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
    suspend fun read(imageFile: SharedImageFile): String {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        return try {
            val fullText = recognize(recognizer, InputImage.fromFilePath(context, Uri.fromFile(imageFile.file)))
            val bottomText = readBottomBand(recognizer, imageFile.file)
            listOf(fullText, bottomText).filter(String::isNotBlank).distinct().joinToString("\n")
        } finally {
            recognizer.close()
        }
    }

    private suspend fun readBottomBand(
        recognizer: com.google.mlkit.vision.text.TextRecognizer,
        file: File
    ): String {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return ""
        val top = (bitmap.height * .62f).toInt().coerceAtMost(bitmap.height - 1)
        val crop = Bitmap.createBitmap(bitmap, 0, top, bitmap.width, bitmap.height - top)
        val scaled = Bitmap.createScaledBitmap(crop, crop.width * 2, crop.height * 2, true)
        return try {
            recognize(recognizer, InputImage.fromBitmap(scaled, 0))
        } finally {
            if (scaled !== crop) scaled.recycle()
            crop.recycle()
            bitmap.recycle()
        }
    }

    private suspend fun recognize(
        recognizer: com.google.mlkit.vision.text.TextRecognizer,
        input: InputImage
    ): String = suspendCancellableCoroutine { continuation ->
        recognizer.process(input)
            .addOnSuccessListener { result ->
                if (continuation.isActive) continuation.resume(result.text)
            }
            .addOnFailureListener { error ->
                if (continuation.isActive) continuation.resumeWithException(error)
            }
    }
}
