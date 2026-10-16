package cl.rafit.memoriadeintenciones

import android.graphics.BitmapFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class IsbnBarcodeFixtureTest {
    @Test
    fun centeredFixtureIsRecognizedByMlKit() {
        val testContext = InstrumentationRegistry.getInstrumentation().context
        val bitmap = testContext.assets.open("isbn-9788466374972.png").use { BitmapFactory.decodeStream(it) }
        requireNotNull(bitmap) { "El fixture ISBN no pudo decodificarse" }

        val scanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_EAN_13)
                .build()
        )
        try {
            val codes = Tasks.await(
                scanner.process(InputImage.fromBitmap(bitmap, 0)),
                10,
                TimeUnit.SECONDS
            )
            assertTrue(
                "ML Kit no reconoció el EAN-13 del fixture: ${codes.map { it.rawValue }}",
                codes.any { it.rawValue == "9788466374972" }
            )
        } finally {
            scanner.close()
            bitmap.recycle()
        }
    }
}
