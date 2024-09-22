package com.sdk.qrcodescanner

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.CoreGraphics.CGRectMake
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
class CameraPreviewView(
    private val cameraPreviewLayer: AVCaptureVideoPreviewLayer
) : UIView(CGRectMake(0.0, 0.0, 0.0, 0.0)) {

    init {
        layer.addSublayer(cameraPreviewLayer)
        cameraPreviewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun layoutSubviews() {
        super.layoutSubviews()

        CATransaction.begin()
        CATransaction.setValue(true, kCATransactionDisableActions)
        cameraPreviewLayer.frame = bounds
        CATransaction.commit()
    }
}