package energy.octopus.octopusenergy.core.util

import android.webkit.WebChromeClient
import android.webkit.WebView
import energy.octopus.octopusenergy.core.logging.Logger

internal class LoadingWebChromeClient(
    val onPageLoaded: () -> Unit,
) : WebChromeClient() {


    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        if (newProgress == 100) {
            onPageLoaded()
            Logger.log("Page loaded ${view?.url}")
        }
    }
}