package com.sdk.qrcodescanner.scanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft

data class FeedbackScreen(
    private val result: String
): Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val nav = LocalNavigator.current
        val copyManager = LocalClipboardManager.current
        val urlHandler = LocalUriHandler.current
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                nav?.pop()
                            }
                        ) {
                            Icon(
                                imageVector = FeatherIcons.ArrowLeft,
                                contentDescription = null
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "Result"
                        )
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(it).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = result,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(30.dp))
                ElevatedButton(
                    onClick = {
                        copyManager.setText(AnnotatedString(result))
                    }
                ) {
                    Text(text = "Copy")
                }
                Spacer(Modifier.height(16.dp))
                ElevatedButton(
                    onClick = {
                        val browserContent = if(matchWebUrl(result)) result else "https://www.google.com/search?q=$result"
                        urlHandler.openUri(uri = browserContent)
                    }
                ) {
                    Text(text = "Open in browser")
                }
            }
        }
    }
    private fun matchWebUrl(url: String): Boolean {
        val regex = Regex(
            """^(https?://)?([a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)+)(:[0-9]{1,5})?(/.*)?$"""
        )
        return regex.matches(url)
    }
}