package cl.rafit.memoriadeintenciones

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import cl.rafit.memoriadeintenciones.domain.Isbn
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

@Composable
@androidx.annotation.OptIn(markerClass = [ExperimentalGetImage::class])
fun IsbnScannerScreen(onFound: (String) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    var granted by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }
    val request = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted = it }
    LaunchedEffect(Unit) { if (!granted) request.launch(Manifest.permission.CAMERA) }
    Column(Modifier.fillMaxSize()) {
        TextButton(onClick = onBack) { Text("← Volver") }
        Text("Escanear ISBN", style = MaterialTheme.typography.headlineLarge)
        if (!granted) { Text("Se necesita la cámara. Puedes volver y completar el libro manualmente.") ; return@Column }
        AndroidView(factory = { viewContext ->
            PreviewView(viewContext).also { previewView ->
                val provider = ProcessCameraProvider.getInstance(viewContext)
                val scanner = BarcodeScanning.getClient(BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_EAN_13).build())
                val scanGate = IsbnScanGate()
                previewView.addOnAttachStateChangeListener(object : android.view.View.OnAttachStateChangeListener {
                    override fun onViewAttachedToWindow(view: android.view.View) = Unit
                    override fun onViewDetachedFromWindow(view: android.view.View) {
                        if (provider.isDone) provider.get().unbindAll()
                        scanner.close()
                    }
                })
                provider.addListener({
                    val camera = provider.get()
                    val preview = Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider }
                    val analysis = ImageAnalysis.Builder().build().also { use -> use.setAnalyzer(ContextCompat.getMainExecutor(viewContext)) { image ->
                        val media = image.image
                        if (media == null) image.close() else scanner.process(com.google.mlkit.vision.common.InputImage.fromMediaImage(media, image.imageInfo.rotationDegrees)).addOnSuccessListener { codes ->
                            val rawValue = codes.firstOrNull()?.rawValue
                            if (rawValue?.let(Isbn::isValid) == true) use.clearAnalyzer()
                            scanGate.emitIfValid(rawValue, onFound)
                        }.addOnCompleteListener { image.close() }
                    } }
                    camera.unbindAll(); camera.bindToLifecycle(context as androidx.lifecycle.LifecycleOwner, androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
                }, ContextCompat.getMainExecutor(viewContext))
            }
        }, modifier = Modifier.fillMaxSize())
    }
}
