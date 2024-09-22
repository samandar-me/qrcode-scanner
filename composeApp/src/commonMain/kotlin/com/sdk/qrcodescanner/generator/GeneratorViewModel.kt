package com.sdk.qrcodescanner.generator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdk.qrcodescanner.core.QrCodeGenerator
import com.sdk.qrcodescanner.core.getGenerator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GeneratorViewModel: ViewModel() {
    var imageByteArray: ByteArray? by mutableStateOf(null)
        private set
    var content: String by mutableStateOf("")
        private set
    var isLoading: Boolean by mutableStateOf(false)
        private set

    fun onQrCodeGenerate() {
        viewModelScope.launch {
            if(content.isBlank())
                return@launch

            isLoading = true
            delay(1000L)
            val qrCodeGenerator: QrCodeGenerator = getGenerator()
            imageByteArray = qrCodeGenerator.generateQrCode(content)
            isLoading = false
        }
    }

    fun onUrlChanged(url: String) {
        content = url
    }
}