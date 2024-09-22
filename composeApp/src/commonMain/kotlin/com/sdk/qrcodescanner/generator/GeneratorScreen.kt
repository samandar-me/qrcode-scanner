package com.sdk.qrcodescanner.generator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.sdk.qrcodescanner.core.PlatformImage
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft

internal object GeneratorScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
       // var platformImage = remember<PlatformImage?> { PlatformImage }
        var url by remember { mutableStateOf("") }
        val nav = LocalNavigator.current
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
                    title = {}
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
//                Image(
//                    bitmap = platformImage?.(), // Use the platform-specific method
//                    contentDescription = null
//                )
                OutlinedTextField(
                    value = url,
                    onValueChange = {
                        url = it
                    },
                    placeholder = {
                        Text(
                            text = "Enter or paste link/url"
                        )
                    }
                )
                Spacer(Modifier.height(24.dp))
                AnimatedVisibility(
                    visible = url.isNotBlank()
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        onClick = {

                        }
                    ) {
                        Text(
                            text = "Generate"
                        )
                    }
                }
            }
        }
    }
}