package energy.octopus.octopusenergy.teslauth

import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.*
import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import energy.octopus.octopusenergy.teslauth.TeslaAuthViewModel.Event.*
import energy.octopus.octopusenergy.teslauth.model.AuthToken
import energy.octopus.octopusenergy.teslauth.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 *  A Composable that shows an embedded [WebView] for Tesla Authentication
 *  @param modifier Modifier to be applied to the button
 *  @param onSuccess callback called with the [AuthToken] as parameter if getting the authorization token was successful,
 *  see response & request at https://tesla-api.timdorr.com/api-basics/authentication#post-https-owner-api.teslamotors.com-oauth-token
 *  @param onError callback called with the [Throwable] that occurred when trying to get the authorization token
 *  @param loadingIndicator optional composable, default is [CircularProgressIndicator]
 */
@Composable
fun TeslAuth(
    modifier: Modifier = Modifier,
    onSuccess: (AuthToken) -> Unit = {},
    onError: (Throwable) -> Unit = {},
    loadingIndicator: @Composable BoxScope.() -> Unit = {
        CircularProgressIndicator(
            Modifier.align(
                Alignment.Center
            )
        )
    },
) {
    val viewModel: TeslaAuthViewModel = viewModel()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(modifier.fillMaxSize()) {
        WebAuth(
            url = viewModel.url,
            modifier = modifier.fillMaxSize(),
            onCodeReceived = viewModel::onAuthorizationCodeReceived,
            onPageLoaded = viewModel::onPageLoaded,
        )
        if (isLoading) {
            loadingIndicator()
        }
    }

    LaunchedEffect(viewModel.event) {
        viewModel.event.onEach {
            when (it) {
                is Success -> onSuccess(it.token)
                is Error -> onError(it.t)
            }
        }.launchIn(this)
    }
}

@Composable
private fun WebAuth(
    url: String,
    modifier: Modifier = Modifier,
    onCodeReceived: (String?) -> Unit = {},
    onPageLoaded: () -> Unit = {},
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                visibility = View.INVISIBLE
                layoutParams = ViewGroup.LayoutParams(
                    MATCH_PARENT,
                    MATCH_PARENT
                )

                settings.apply {
                    userAgentString = "UA"
                    // TODO Check if there's a way to authenticate without enabling JavaScript
                    javaScriptEnabled = true
                }
                webViewClient = TeslaWebViewClient(onCodeParsed = onCodeReceived)
                webChromeClient = LoadingWebChromeClient {
                    visibility = View.VISIBLE
                    onPageLoaded()
                }
                Log.i(TESLA_AUTH_TAG, "User agent: ${settings.userAgentString}")
                Log.i(TESLA_AUTH_TAG, "Opening url for login: $url")
                loadUrl(url)
                setBackListener()
            }
        }
    )
}

private fun WebView.setBackListener() {
    setOnKeyListener { _, keyCode, keyEvent ->
        if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK && canGoBack()) {
            goBack()
            true
        } else {
            false
        }
    }
}