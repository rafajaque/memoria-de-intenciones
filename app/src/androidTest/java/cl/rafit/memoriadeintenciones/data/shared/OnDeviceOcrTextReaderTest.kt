package cl.rafit.memoriadeintenciones.data.shared

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.io.File
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnDeviceOcrTextReaderTest {
    @Test
    fun readsTextFromAnAppPrivateSyntheticImage() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val source = File(context.cacheDir, "ocr-local-fixture.png")
        val directory = File(context.filesDir, "ocr-reader-test")
        directory.deleteRecursively()

        try {
            val bitmap = Bitmap.createBitmap(1200, 360, Bitmap.Config.ARGB_8888)
            try {
                Canvas(bitmap).apply {
                    drawColor(Color.WHITE)
                    drawText(
                        "MEMORIA OCR",
                        70f,
                        220f,
                        Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            color = Color.BLACK
                            textSize = 112f
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                        }
                    )
                }
                source.outputStream().use { output ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                }
            } finally {
                bitmap.recycle()
            }

            val privateImage = source.inputStream().use { input ->
                PrivateImageCopier(directory).copy("image/png", input)
            }
            val detectedText = OnDeviceOcrTextReader(context).read(privateImage)

            assertTrue(detectedText.uppercase().contains("MEMORIA OCR"))
        } finally {
            source.delete()
            directory.deleteRecursively()
        }
    }
}
