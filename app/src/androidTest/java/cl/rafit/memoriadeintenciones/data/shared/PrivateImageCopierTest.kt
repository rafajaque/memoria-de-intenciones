package cl.rafit.memoriadeintenciones.data.shared

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.io.ByteArrayInputStream
import java.io.File
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PrivateImageCopierTest {
    private lateinit var directory: File

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        directory = File(context.filesDir, "shared-image-copier-test")
        directory.deleteRecursively()
    }

    @After
    fun tearDown() {
        directory.deleteRecursively()
    }

    @Test
    fun copyStoresAnImageOnlyInsideItsPrivateDirectory() {
        val copied = PrivateImageCopier(directory, maxBytes = 10)
            .copy("image/png", ByteArrayInputStream(byteArrayOf(1, 2, 3)))

        assertTrue(copied.file.exists())
        assertEquals(directory.canonicalPath, copied.file.parentFile?.canonicalPath)
        assertEquals("image/png", copied.mimeType)
        assertEquals(3, copied.byteCount)
    }

    @Test
    fun copyRejectsUnsupportedOrOversizedContentWithoutLeavingAFile() {
        val copier = PrivateImageCopier(directory, maxBytes = 3)

        val unsupported = runCatching {
            copier.copy("text/plain", ByteArrayInputStream(byteArrayOf(1)))
        }.exceptionOrNull()
        assertTrue(unsupported is SharedImageImportException.UnsupportedMimeType)
        assertFalse(directory.exists())

        val tooLarge = runCatching {
            copier.copy("image/jpeg", ByteArrayInputStream(byteArrayOf(1, 2, 3, 4)))
        }.exceptionOrNull()
        assertTrue(tooLarge is SharedImageImportException.FileTooLarge)
        assertTrue(directory.listFiles().isNullOrEmpty())
    }
}
