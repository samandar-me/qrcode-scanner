import androidx.compose.ui.window.ComposeUIViewController
import com.sdk.qrcodescanner.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
