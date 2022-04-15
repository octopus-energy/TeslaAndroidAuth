package energy.octopus.octopusenergy.core.util

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import energy.octopus.octopusenergy.core.logging.Logger

internal class AuthWebViewClient(
    private val onCodeParsed: (String?) -> Unit,
    private val redirectUri: String,
    private val resultParameterKey: String = "code"
) : WebViewClient() {

    var code: String? = null
        private set(value) {
            field = value
            onCodeParsed(value)
            Logger.log("Parsed code $value")
        }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) =
        request?.url?.toString()?.let {
            if (it.startsWith(redirectUri)) {
                code = it.substringAfter("$resultParameterKey=").substringBefore("&")
                return@let true
            }
            false
        } ?: false
}