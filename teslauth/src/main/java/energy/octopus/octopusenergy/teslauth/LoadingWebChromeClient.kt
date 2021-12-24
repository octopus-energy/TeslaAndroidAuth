package energy.octopus.octopusenergy.teslauth

import android.webkit.WebChromeClient
import android.webkit.WebView

internal class LoadingWebChromeClient(
    val onPageLoaded: () -> Unit,
) : WebChromeClient() {


    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        if (newProgress == 100) {
            onPageLoaded()
        }
    }
}