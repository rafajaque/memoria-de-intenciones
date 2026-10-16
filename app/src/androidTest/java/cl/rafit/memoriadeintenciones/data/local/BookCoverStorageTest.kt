package cl.rafit.memoriadeintenciones.data.local

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.io.ByteArrayOutputStream
import java.io.File
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookCoverStorageTest {
    @Test
    fun downloadsValidHttpsCoverIntoPrivateStorageAndDeletesIt() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val bytes = onePixelPng()
        val storage = BookCoverStorage(context, CoverDownloader { bytes })

        val path = storage.download("https://example.test/cover.png", "9780306406157")

        assertNotNull(path)
        val file = File(path!!)
        assertTrue(file.isFile)
        storage.delete(path)
        assertFalse(file.exists())
    }

    @Test
    fun ignoresNonHttpsOrInvalidImages() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val storage = BookCoverStorage(context, CoverDownloader { byteArrayOf(1, 2, 3) })

        assertTrue(storage.download("http://example.test/cover.png", "9780306406157") == null)
        assertTrue(storage.download("https://example.test/cover.png", "9780306406157") == null)
    }

    private fun onePixelPng(): ByteArray = ByteArrayOutputStream().use { output ->
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).apply {
            compress(Bitmap.CompressFormat.PNG, 100, output)
            recycle()
        }
        output.toByteArray()
    }
}
