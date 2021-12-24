package energy.octopus.octopusenergy.teslauth

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import energy.octopus.octopusenergy.teslauth.util.TESLA_AUTH_TAG

internal class TeslaWebViewClient(
    val onCodeParsed: (String?) -> Unit,
) : WebViewClient() {

    var code: String? = null
        private set(value) {
            field = value
            onCodeParsed(value)
            Log.i(TESLA_AUTH_TAG, "Parsed code $value")
        }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) =
        request?.url?.toString()?.let {
            if (it.startsWith("https://auth.tesla.com/void/callback?")) {
                code = it.substringAfter("code=").substringBefore("&")
                return@let true
            }
            false
        } ?: false
}