package com.sdk.qrcodescanner.scanner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException
import kotlinx.coroutines.launch

class ScannerViewModel(
    private val controller: PermissionsController
): ViewModel() {
    var permissionState by mutableStateOf(PermissionState.NotDetermined)
        private set

    init {
        viewModelScope.launch {
            permissionState = controller.getPermissionState(Permission.CAMERA)
        }
    }

    fun requestCameraPermission() {
        viewModelScope.launch {
            try {
                controller.providePermission(Permission.CAMERA)
                permissionState = PermissionState.Granted
            } catch(e: DeniedAlwaysException) {
                permissionState = PermissionState.DeniedAlways
            } catch(e: DeniedException) {
                permissionState = PermissionState.Denied
            } catch(e: RequestCanceledException) {
                e.printStackTrace()
            }
        }
    }
}