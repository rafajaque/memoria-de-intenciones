package cl.rafit.memoriadeintenciones.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import java.io.File
import coil.compose.SubcomposeAsyncImage
import coil.ImageLoader
import cl.rafit.memoriadeintenciones.LocalLibraryMaterials

@Composable
fun BookCover(
    url: String?,
    title: String,
    modifier: Modifier = Modifier,
    localPath: String? = null
) {
    val context = LocalContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context)
            .diskCache(null)
            .build()
    }
    SubcomposeAsyncImage(
        model = localPath?.takeIf { File(it).isFile }?.let(::File)
            ?: url?.takeIf(String::isNotBlank)
            ?: "invalid://book-cover",
        contentDescription = "Portada de $title",
        imageLoader = imageLoader,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .semantics { contentDescription = "Portada de $title" },
        loading = { _ -> BookCoverPlaceholder() },
        error = { _ -> BookCoverPlaceholder() }
    )
}

@Composable
private fun BookCoverPlaceholder() {
    val materials = LocalLibraryMaterials.current
    Box(
        Modifier
            .fillMaxSize()
            .background(materials.recess),
        contentAlignment = Alignment.Center
    ) {
        BookplateMark(Modifier.fillMaxSize(.55f))
    }
}
