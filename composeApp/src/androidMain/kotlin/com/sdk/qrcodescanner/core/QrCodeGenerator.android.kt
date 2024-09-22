package com.sdk.qrcodescanner.core

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asImageBitmap

actual class PlatformImage(val bitmap: Bitmap)

fun PlatformImage.asComposeImage(): androidx.compose.ui.graphics.ImageBitmap {
    return bitmap.asImageBitmap()
}