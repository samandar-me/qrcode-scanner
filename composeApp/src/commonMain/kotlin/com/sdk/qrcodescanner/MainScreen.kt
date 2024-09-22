package com.sdk.qrcodescanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.sdk.qrcodescanner.generator.GeneratorScreen
import com.sdk.qrcodescanner.scanner.ScannerScreen
import compose.icons.FeatherIcons
import compose.icons.feathericons.Link2
import compose.icons.feathericons.Maximize

internal object MainScreen: Screen {

    @Composable
    override fun Content() {
        val nav = LocalNavigator.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "QR Code Scanner",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(50.dp))
            Button(
                onClick = {
                    nav?.push(ScannerScreen)
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Scanner")
                    Spacer(Modifier.width(12.dp))
                    Icon(imageVector = FeatherIcons.Maximize, contentDescription = null)
                }
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    nav?.push(GeneratorScreen)
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Generate QR Code")
                    Spacer(Modifier.width(12.dp))
                    Icon(imageVector = FeatherIcons.Link2, contentDescription = null)
                }
            }
        }
    }
}
