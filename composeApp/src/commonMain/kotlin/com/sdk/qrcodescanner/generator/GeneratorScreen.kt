package com.sdk.qrcodescanner.generator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.sdk.qrcodescanner.core.byteArrayToImageBitmap
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft

internal object GeneratorScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val nav = LocalNavigator.current

        val imeBottomInset = WindowInsets.ime.getBottom(LocalDensity.current)
        val imeBottomInsetDp = with(LocalDensity.current) { imeBottomInset.toDp() }
        val keyboardController = LocalSoftwareKeyboardController.current
        val vm = viewModel {  GeneratorViewModel() }

        Scaffold(
            modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                detectTapGestures {
                    keyboardController?.hide()
                }
            },
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
                    .padding(horizontal = 20.dp)
                    .padding(bottom = imeBottomInsetDp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val image = byteArrayToImageBitmap(vm.imageByteArray)
                Spacer(Modifier.padding(it).height(20.dp))
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .border(
                            BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    this@Column.AnimatedVisibility(
                        visible = vm.isLoading
                    ) {
                        CircularProgressIndicator()
                    }
                    this@Column.AnimatedVisibility(
                        visible = !vm.isLoading
                    ) {
                        if (image != null) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                bitmap = image,
                                contentDescription = null
                            )
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
                OutlinedTextField(
                    value = vm.content,
                    onValueChange = vm::onUrlChanged,
                    placeholder = {
                        Text(
                            text = "Enter or paste link/url"
                        )
                    }
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    onClick = vm::onQrCodeGenerate,
                    enabled = !vm.isLoading
                ) {
                    Text(
                        text = "Generate"
                    )
                }
            }
        }
    }
}