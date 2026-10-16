package cl.rafit.memoriadeintenciones.data.shared

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.io.File
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnDeviceIsbnReaderTest {
    @Test fun readsIsbnFromBarcodeFixture() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val directory = File(context.filesDir, "isbn-reader-test")
        directory.deleteRecursively()
        val image = context.assets.open("isbn-9788466374972.png").use { input ->
            PrivateImageCopier(directory).copy("image/png", input)
        }

        try {
            assertEquals("9788466374972", OnDeviceIsbnReader(context).find(image))
        } finally {
            directory.deleteRecursively()
        }
    }
}
