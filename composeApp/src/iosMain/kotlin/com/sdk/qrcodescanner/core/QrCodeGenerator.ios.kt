package com.sdk.qrcodescanner.core

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.set
import platform.CoreGraphics.CGAffineTransformMakeScale
import platform.CoreImage.CIContext
import platform.CoreImage.CIFilter
import platform.CoreImage.createCGImage
import platform.CoreImage.filterWithName
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.Foundation.setValue
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation

class QRCodeGeneratorImpl : QrCodeGenerator {

    @OptIn(ExperimentalForeignApi::class)
    override fun generateQrCode(content: String): ByteArray {
        try {
            val data = content.encodeToByteArray().toNSData()
            val filter = CIFilter.filterWithName("CIQRCodeGenerator")
            filter?.setValue(data, forKey = "inputMessage")
            filter?.setValue("M", forKey = "inputCorrectionLevel")

            val qrCodeImage = filter?.outputImage ?: return ByteArray(0)

            val transform = CGAffineTransformMakeScale(10.0, 10.0)
            val scaledQrCodeImage = qrCodeImage.imageByApplyingTransform(transform)

            val context = CIContext.context()
            val cgImage = context.createCGImage(scaledQrCodeImage, scaledQrCodeImage.extent) ?: return ByteArray(0)
            val uiImage = UIImage(cgImage)
            val pngData = UIImagePNGRepresentation(uiImage)
            return pngData?.bytes?.readBytes(pngData.length.toInt()) ?: ByteArray(0)
        } catch (e: Exception) {
            println("@@@@ ${e.message}")
            return ByteArray(0)
        }
    }


    @OptIn(ExperimentalForeignApi::class)
    private fun ByteArray.toNSData(): NSData {
        return memScoped {
            val byteArrayPointer: CPointer<ByteVar> = this.allocArray<ByteVar>(this@toNSData.size).apply {
                this@toNSData.forEachIndexed { index, byte ->
                    this[index] = byte
                }
            }
            NSData.dataWithBytes(byteArrayPointer, this@toNSData.size.convert())
        }
    }
}

actual fun getGenerator(): QrCodeGenerator = QRCodeGeneratorImpl()