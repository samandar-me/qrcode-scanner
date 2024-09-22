import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import com.sdk.qrcodescanner.App
import platform.UIKit.UIViewController


fun MainViewController(): UIViewController = ComposeUIViewController(
    configure = {
        this.onFocusBehavior = OnFocusBehavior.DoNothing
    }
) { App() }
