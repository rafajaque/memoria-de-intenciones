package cl.rafit.memoriadeintenciones.data.shared

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import cl.rafit.memoriadeintenciones.domain.Isbn
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class OnDeviceIsbnReader(private val context: Context) {
    suspend fun find(imageFile: SharedImageFile): String? {
        val scanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_EAN_13).build()
        )
        return try {
            val fullImage = InputImage.fromFilePath(context, Uri.fromFile(imageFile.file))
            findValid(scanner, fullImage) ?: readBottomBand(scanner, imageFile.file)
        } finally {
            scanner.close()
        }
    }

    private suspend fun readBottomBand(scanner: com.google.mlkit.vision.barcode.BarcodeScanner, file: File): String? {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return null
        val top = (bitmap.height * .62f).toInt().coerceAtMost(bitmap.height - 1)
        val crop = Bitmap.createBitmap(bitmap, 0, top, bitmap.width, bitmap.height - top)
        val scaled = Bitmap.createScaledBitmap(crop, crop.width * 2, crop.height * 2, true)
        return try {
            findValid(scanner, InputImage.fromBitmap(scaled, 0))
        } finally {
            if (scaled !== crop) scaled.recycle()
            crop.recycle()
            bitmap.recycle()
        }
    }

    private suspend fun findValid(
        scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
        image: InputImage
    ): String? = suspendCoroutine { continuation ->
        scanner.process(image)
            .addOnSuccessListener { codes ->
                continuation.resume(codes.asSequence().mapNotNull { it.rawValue }.firstOrNull(Isbn::isValid))
            }
            .addOnFailureListener { continuation.resume(null) }
    }
}
