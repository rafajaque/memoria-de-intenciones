package cl.rafit.memoriadeintenciones.data.shared

import java.io.File
import java.io.InputStream
import java.util.UUID

// MVP: valida y copia una captura al espacio privado antes de entregarla al OCR.
class PrivateImageCopier(
    private val targetDirectory: File,
    private val maxBytes: Long = MAX_IMAGE_BYTES
) {
    fun copy(mimeType: String?, source: InputStream): SharedImageFile {
        val acceptedMimeType = mimeType?.lowercase()?.takeIf { it in supportedMimeTypes }
            ?: throw SharedImageImportException.UnsupportedMimeType(mimeType)

        if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
            throw SharedImageImportException.PrivateStorageUnavailable
        }

        val extension = extensions.getValue(acceptedMimeType)
        val destination = File(targetDirectory, "capture-${UUID.randomUUID()}.$extension")
        val temporary = File(targetDirectory, ".${destination.name}.tmp")
        var byteCount = 0L

        try {
            source.buffered().use { input ->
                temporary.outputStream().buffered().use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break
                        byteCount += read
                        if (byteCount > maxBytes) {
                            throw SharedImageImportException.FileTooLarge(maxBytes)
                        }
                        output.write(buffer, 0, read)
                    }
                }
            }
            if (!temporary.renameTo(destination)) {
                throw SharedImageImportException.PrivateStorageUnavailable
            }
            return SharedImageFile(destination, acceptedMimeType, byteCount)
        } catch (error: Exception) {
            temporary.delete()
            destination.delete()
            throw error
        }
    }

    private companion object {
        const val MAX_IMAGE_BYTES = 15L * 1024L * 1024L
        val supportedMimeTypes = setOf("image/jpeg", "image/png", "image/webp")
        val extensions = mapOf(
            "image/jpeg" to "jpg",
            "image/png" to "png",
            "image/webp" to "webp"
        )
    }
}

data class SharedImageFile(
    val file: File,
    val mimeType: String,
    val byteCount: Long
)

sealed class SharedImageImportException(message: String) : Exception(message) {
    data class UnsupportedMimeType(val received: String?) :
        SharedImageImportException("El formato compartido no es una imagen compatible.")

    data class FileTooLarge(val maxBytes: Long) :
        SharedImageImportException("La captura supera el tamaño máximo permitido.")

    data object PrivateStorageUnavailable :
        SharedImageImportException("No fue posible preparar el almacenamiento privado.")
}
