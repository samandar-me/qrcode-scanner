package com.sdk.qrcodescanner

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.sdk.qrcodescanner.theme.AppTheme

@Composable
internal fun App() = AppTheme {
    Navigator(MainScreen) {
        FadeTransition(it)
    }
}
