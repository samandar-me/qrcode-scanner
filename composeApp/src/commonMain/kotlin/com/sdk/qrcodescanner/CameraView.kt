package com.sdk.qrcodescanner

import androidx.compose.runtime.Composable

@Composable
expect fun CameraView(
    onQrCodeScanned: (String) -> Unit
)