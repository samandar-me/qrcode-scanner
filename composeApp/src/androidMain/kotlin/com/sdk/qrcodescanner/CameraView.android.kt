package com.sdk.qrcodescanner

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.sdk.qrcodescanner.core.QrCodeAnalyzer
import compose.icons.FeatherIcons
import compose.icons.feathericons.Zap
import compose.icons.feathericons.ZapOff
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
actual fun CameraView(
    onQrCodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var preview by remember { mutableStateOf<Preview?>(null) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var torchState by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .height(400.dp)
            .width(300.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(12.dp)
            )
    ) {
        key(torchState) {
            AndroidView(
                factory = { androidViewContext ->
                    PreviewView(androidViewContext).apply {
                        this.scaleType = PreviewView.ScaleType.FILL_CENTER
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { previewView ->
                    val cameraSelector: CameraSelector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                    val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

                    cameraProviderFuture.addListener({
                        preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                        val barcodeAnalyser = QrCodeAnalyzer { barcodes ->
                            barcodes.forEach { barcode ->
                                barcode.rawValue?.let { barcodeValue ->
                                    onQrCodeScanned(barcodeValue)
                                    val vibrator =
                                        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                    if (Build.VERSION.SDK_INT >= 26) {
                                        vibrator.vibrate(
                                            VibrationEffect.createOneShot(
                                                100,
                                                VibrationEffect.DEFAULT_AMPLITUDE
                                            )
                                        )
                                    } else {
                                        vibrator.vibrate(100)
                                    }
                                }
                            }
                        }
                        val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(cameraExecutor, barcodeAnalyser)
                            }

                        try {
                            cameraProvider.unbindAll()
                            val camera = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                            camera.cameraControl.enableTorch(torchState)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.e("qr code", e.message ?: "")
                        }
                    }, ContextCompat.getMainExecutor(context))
                })
        }
        IconButton(
            modifier = Modifier.padding(8.dp).align(Alignment.BottomCenter),
            onClick = {
                torchState = !torchState
            }
        ) {
            Icon(
                imageVector = if (torchState) FeatherIcons.Zap else FeatherIcons.ZapOff,
                contentDescription = null
            )
        }
    }

    DisposableEffect(cameraProviderFuture) {
        onDispose {
            cameraProviderFuture.get().unbindAll()
        }
    }
}
