package com.sdk.qrcodescanner.scanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.sdk.qrcodescanner.CameraView
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory

internal object ScannerScreen: Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val nav = LocalNavigator.current
        val factory = rememberPermissionsControllerFactory()
        val controller = remember(factory) {
            factory.createPermissionsController()
        }

        BindEffect(controller)

        val vm = viewModel {
            ScannerViewModel(controller)
        }

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
                when (vm.permissionState) {
                    PermissionState.Granted -> {
                        Text(
                            text = "Place the code inside the frame"
                        )
                        Spacer(Modifier.height(16.dp))
                        CameraView { result ->
                            nav?.push(FeedbackScreen(result))
                        }
                        Spacer(Modifier.height(30.dp))
                    }

                    PermissionState.DeniedAlways -> {
                        Text("Permission was permanently declined.")
                        Button(onClick = {
                            controller.openAppSettings()
                        }) {
                            Text("Open app settings")
                        }
                    }

                    else -> {
                        Button(
                            onClick = {
                                vm.requestCameraPermission()
                            }
                        ) {
                            Text(text = "First, give access permission for camera")
                        }
                    }
                }
            }
        }
    }
}

