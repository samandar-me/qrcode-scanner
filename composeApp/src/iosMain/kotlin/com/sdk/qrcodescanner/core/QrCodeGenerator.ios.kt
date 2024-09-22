package com.sdk.qrcodescanner.core

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.set

class QRCodeGeneratorImpl : QrCodeGenerator {

    override fun generateQrCode(content: String): ByteArray {
        try {
            val data = content.encodeToByteArray().toNSData()
            val filter = CIFilter.filterWithName("CIQRCodeGenerator")
            filter?.setValue(data, forKey = "inputMessage")
            // Use a valid correction level (L, M, Q, H)
            filter?.setValue("M", forKey = "inputCorrectionLevel")

            val qrCodeImage = filter?.outputImage ?: return ByteArray(0)
            val context = CIContext.context()
            val cgImage = context.createCGImage(qrCodeImage, qrCodeImage.extent) ?: return ByteArray(0)

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