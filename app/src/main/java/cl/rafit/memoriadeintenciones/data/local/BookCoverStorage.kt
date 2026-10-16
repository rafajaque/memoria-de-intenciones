package cl.rafit.memoriadeintenciones.data.local

import android.content.Context
import android.graphics.BitmapFactory
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun interface CoverDownloader {
    fun download(url: String): ByteArray
}

private class HttpCoverDownloader : CoverDownloader {
    override fun download(url: String): ByteArray {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 8_000
            readTimeout = 8_000
            requestMethod = "GET"
            instanceFollowRedirects = true
        }
        return try {
            val response = connection
            if (response.responseCode !in 200..299) error("HTTP ${response.responseCode}")
            response.inputStream.use { input -> input.readBytes().also { bytes ->
                require(bytes.size <= BookCoverStorage.MAX_COVER_BYTES) { "La portada supera el límite permitido" }
            } }
        } finally {
            connection.disconnect()
        }
    }
}

class BookCoverStorage(
    context: Context,
    private val downloader: CoverDownloader = HttpCoverDownloader()
) {
    private val directory = File(context.applicationContext.filesDir, "book-covers")

    suspend fun download(url: String?, isbn: String): String? = withContext(Dispatchers.IO) {
        val remoteUrl = url?.trim()?.takeIf { it.startsWith("https://") } ?: return@withContext null
        runCatching {
            val bytes = downloader.download(remoteUrl)
            require(BitmapFactory.decodeByteArray(bytes, 0, bytes.size) != null) { "La portada no es una imagen válida" }
            directory.mkdirs()
            val target = File(directory, "${isbn.filter(Char::isDigit)}.img")
            val temporary = File(directory, "${target.name}.tmp")
            temporary.writeBytes(bytes)
            check(temporary.renameTo(target)) { "No se pudo guardar la portada" }
            target.absolutePath
        }.getOrNull()
    }

    suspend fun delete(path: String?) = withContext(Dispatchers.IO) {
        val file = path?.let(::File) ?: return@withContext
        if (file.parentFile?.canonicalFile == directory.canonicalFile) file.delete()
    }

    companion object {
        const val MAX_COVER_BYTES = 10 * 1024 * 1024
    }
}
