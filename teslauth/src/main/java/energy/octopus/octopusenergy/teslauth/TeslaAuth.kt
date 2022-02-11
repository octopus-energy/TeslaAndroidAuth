package energy.octopus.octopusenergy.teslauth

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
import energy.octopus.octopusenergy.teslauth.logging.LogLevel
import energy.octopus.octopusenergy.teslauth.logging.Logger
import energy.octopus.octopusenergy.teslauth.model.AuthToken
import energy.octopus.octopusenergy.teslauth.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


typealias OnAuthorizationCodeReceivedCallback = (String) -> Unit
typealias OnTokenReceivedCallback = (AuthToken) -> Unit

/**
 *  A Composable that shows an embedded [WebView] for Tesla Authentication
 *  @param modifier Modifier to be applied to the button
 *  @param logLevel specifies the [LogLevel] to be used
 *  @param onAuthorizationCodeReceived callback called with the code represented by a [String] as parameter if getting the authorization code was successful,
 *  @see <a href="https://tesla-api.timdorr.com/api-basics/authentication#step-2-obtain-an-authorization-code">Step 2: Obtain an authorization code</a>
 *  @param onBearerTokenReceived callback called with the [AuthToken] as parameter if getting the bearer token was successful,
 *  @see <a href="https://tesla-api.timdorr.com/api-basics/authentication#step-3-exchange-authorization-code-for-bearer-token">Step 3: Exchange authorization code for bearer token</a>
 *  @param onAccessTokenReceived callback called with the [AuthToken] as parameter if getting the authorization token was successful,
 *  @see <a href="https://tesla-api.timdorr.com/api-basics/authentication#refreshing-an-access-token">https://tesla-api.timdorr.com/api-basics/authentication#refreshing-an-access-token</a>
 *  @param onError callback called with the [Throwable] that occurred when trying to get the authorization token
 *  @param onDismiss called when the underlying [WebView] can't go back and the back press represents a dismiss of the Authentication WebView
 *  @param loadingIndicator optional composable, default is [CircularProgressIndicator]
 */

@Composable
fun TeslAuth(
    modifier: Modifier = Modifier,
    logLevel: LogLevel = LogLevel.EMPTY,
    onAuthorizationCodeReceived: OnAuthorizationCodeReceivedCallback? = null,
    onBearerTokenReceived: OnTokenReceivedCallback? = null,
    onAccessTokenReceived: OnTokenReceivedCallback? = null,
    onError: (Throwable) -> Unit = {},
    onDismiss: () -> Unit = {},
    loadingIndicator: @Composable BoxScope.() -> Unit = {
        CircularProgressIndicator(
            Modifier.align(
                Alignment.Center
            )
        )
    },
) {
    Logger.level = logLevel
    val viewModel: TeslaAuthViewModel = viewModel()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(modifier.fillMaxSize()) {
        WebAuth(
            url = viewModel.url,
            modifier = modifier.fillMaxSize(),
            onCodeReceived = viewModel::onCodeReceived,
            onPageLoaded = viewModel::onPageLoaded,
            onDismiss = onDismiss,
        )
        if (isLoading) {
            loadingIndicator()
        }
    }

    LaunchedEffect(viewModel.event) {
        viewModel.event.onEach {
            when (it) {
                is ReceivedBearerToken -> {
                    onBearerTokenReceived?.invoke(it.token)
                    if (onAccessTokenReceived != null) {
                        viewModel.getAccessToken(it.token)
                    }
                }
                is ReceivedAccessToken -> onAccessTokenReceived?.invoke(it.token)
                is Error -> onError(it.t)
                is AuthorizationCodeReceived -> if (onBearerTokenReceived != null || onAccessTokenReceived != null) {
                    viewModel.getBearerToken(it.code)
                }
                Dismiss -> onDismiss()
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
    onDismiss: () -> Unit = {},
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