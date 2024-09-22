package com.sdk.qrcodescanner.core

import platform.UIKit.UIImage

actual class PlatformImage(val uiImage: UIImage)

fun PlatformImage.asComposeImage(): UIImage = uiImage