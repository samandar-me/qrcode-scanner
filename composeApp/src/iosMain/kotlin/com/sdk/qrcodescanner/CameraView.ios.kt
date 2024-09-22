package com.sdk.qrcodescanner

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import compose.icons.FeatherIcons
import compose.icons.feathericons.Zap
import compose.icons.feathericons.ZapOff
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceDiscoverySession.Companion.discoverySessionWithDeviceTypes
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureDeviceInput.Companion.deviceInputWithDevice
import platform.AVFoundation.AVCaptureDevicePositionBack
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInDualCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInDualWideCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInDuoCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInUltraWideCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInWideAngleCamera
import platform.AVFoundation.AVCaptureMetadataOutput
import platform.AVFoundation.AVCaptureMetadataOutputObjectsDelegateProtocol
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCapturePhotoOutput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureSessionPresetPhoto
import platform.AVFoundation.AVCaptureTorchModeOff
import platform.AVFoundation.AVCaptureTorchModeOn
import platform.AVFoundation.AVCaptureVideoOrientationLandscapeLeft
import platform.AVFoundation.AVCaptureVideoOrientationLandscapeRight
import platform.AVFoundation.AVCaptureVideoOrientationPortrait
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVMetadataMachineReadableCodeObject
import platform.AVFoundation.hasTorch
import platform.AVFoundation.isTorchActive
import platform.AVFoundation.torchActive
import platform.AVFoundation.torchMode
import platform.AudioToolbox.AudioServicesPlaySystemSound
import platform.AudioToolbox.kSystemSoundID_Vibrate
import platform.CoreGraphics.CGRect
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSSelectorFromString
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceOrientation
import platform.UIKit.UIDeviceOrientationDidChangeNotification
import platform.UIKit.UIView
import platform.darwin.NSObject
import platform.darwin.dispatch_get_main_queue
import kotlin.OptIn
import kotlin.String
import kotlin.Unit
import kotlin.also
import kotlin.let


@Composable
actual fun CameraView(
    onQrCodeScanned: (String) -> Unit
) {
    val camera: AVCaptureDevice? = remember {
        discoverySessionWithDeviceTypes(
            deviceTypes = listOf(
                AVCaptureDeviceTypeBuiltInWideAngleCamera,
                AVCaptureDeviceTypeBuiltInDualWideCamera,
                AVCaptureDeviceTypeBuiltInDualCamera,
                AVCaptureDeviceTypeBuiltInUltraWideCamera,
                AVCaptureDeviceTypeBuiltInDuoCamera
            ),
            mediaType = AVMediaTypeVideo,
            position = AVCaptureDevicePositionBack,
        ).devices.firstOrNull() as? AVCaptureDevice
    }
    if (camera != null) {
        DeviceCamera(camera, onQrCodeScanned)
    } else {
        Text(
            text = "Camera is not available."
        )
    }
}

@OptIn(ExperimentalForeignApi::class, DelicateCoroutinesApi::class)
@Composable
private fun DeviceCamera(
    camera: AVCaptureDevice,
    onQrCodeScanned: (String) -> Unit
) {
    val capturePhotoOutput = remember { AVCapturePhotoOutput() }

    val captureSession: AVCaptureSession = remember {
        AVCaptureSession().also { captureSession ->
            captureSession.sessionPreset = AVCaptureSessionPresetPhoto
            val captureDeviceInput: AVCaptureDeviceInput =
                deviceInputWithDevice(device = camera, error = null)!!
            captureSession.addInput(captureDeviceInput)
            captureSession.addOutput(capturePhotoOutput)
            val metadataOutput = AVCaptureMetadataOutput()
            if (captureSession.canAddOutput(metadataOutput)) {
                captureSession.addOutput(metadataOutput)
                metadataOutput.setMetadataObjectsDelegate(objectsDelegate = object : NSObject(),
                    AVCaptureMetadataOutputObjectsDelegateProtocol {
                    override fun captureOutput(
                        output: AVCaptureOutput,
                        didOutputMetadataObjects: List<*>,
                        fromConnection: AVCaptureConnection
                    ) {
                        didOutputMetadataObjects.firstOrNull()?.let { metadataObject ->
                            val readableObject =
                                metadataObject as? AVMetadataMachineReadableCodeObject
                            val code = readableObject?.stringValue ?: ""
                            AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
                            onQrCodeScanned(code)
                            captureSession.stopRunning()
                        }
                    }
                }, queue = dispatch_get_main_queue())
                metadataOutput.metadataObjectTypes = metadataOutput.availableMetadataObjectTypes()
            }
        }
    }
    val cameraPreviewLayer = remember { AVCaptureVideoPreviewLayer(session = captureSession) }

    DisposableEffect(Unit) {
        captureSession.startRunning()
        onDispose {
            GlobalScope.launch(Dispatchers.IO) {
                captureSession.stopRunning()
            }
        }
    }

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
        val view = remember {
            CameraPreviewView(cameraPreviewLayer).apply {
                captureSession.startRunning()
            }
        }

        UIKitView(
            factory = { view },
            modifier = Modifier.fillMaxSize(),
            properties = UIKitInteropProperties(
                isInteractive = true,
                isNativeAccessibilityEnabled = true
            )
        )
        IconButton(
            modifier = Modifier.padding(8.dp).align(Alignment.BottomCenter),
            onClick = {
                camera.lockForConfiguration(null)
                if (!camera.hasTorch())
                    return@IconButton

                camera.torchMode =
                    if (camera.isTorchActive()) AVCaptureTorchModeOff else AVCaptureTorchModeOn

                camera.unlockForConfiguration()
            }
        ) {
            Icon(
                imageVector = FeatherIcons.Zap,
                contentDescription = null
            )
        }
    }
}

