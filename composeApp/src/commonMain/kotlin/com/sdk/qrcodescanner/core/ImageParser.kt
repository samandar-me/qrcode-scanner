package com.sdk.qrcodescanner.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

@Composable
expect fun byteArrayToImageBitmap(byteArray: ByteArray?): ImageBitmap?