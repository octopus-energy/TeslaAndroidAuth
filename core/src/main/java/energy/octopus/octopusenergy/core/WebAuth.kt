package energy.octopus.octopusenergy.core

import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import energy.octopus.octopusenergy.core.logging.Logger
import energy.octopus.octopusenergy.core.util.AuthWebViewClient
import energy.octopus.octopusenergy.core.util.LoadingWebChromeClient

@Composable
fun WebAuth(
    url: String,
    redirectUri: String,
    modifier: Modifier = Modifier,
    onCodeReceived: (String?) -> Unit = {},
    onPageLoaded: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                visibility = View.INVISIBLE
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.apply {
                    userAgentString = "UA"
                    // TODO Check if there's a way to authenticate without enabling JavaScript
                    javaScriptEnabled = true
                }
                webViewClient = AuthWebViewClient(
                    onCodeParsed = onCodeReceived,
                    redirectUri = redirectUri,
                )
                webChromeClient = LoadingWebChromeClient {
                    visibility = View.VISIBLE
                    onPageLoaded()
                }
                Logger.log("User agent: ${settings.userAgentString}")
                Logger.log("Opening url for login: $url")
                loadUrl(url)
                setBackListener(onDismiss = onDismiss)
            }
        }
    )
}

private fun WebView.setBackListener(onDismiss: () -> Unit = {}) {
    setOnKeyListener { _, keyCode, keyEvent ->
        if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            onDismiss()
            true
        } else {
            false
        }
    }
}