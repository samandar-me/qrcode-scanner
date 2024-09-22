package com.sdk.qrcodescanner.core

interface QrCodeGenerator {
    fun generateQrCode(content: String): ByteArray
}
expect fun getGenerator(): QrCodeGenerator