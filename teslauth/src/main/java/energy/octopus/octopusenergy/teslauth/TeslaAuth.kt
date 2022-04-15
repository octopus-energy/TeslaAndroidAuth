package energy.octopus.octopusenergy.teslauth

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
import androidx.lifecycle.viewmodel.compose.viewModel
import energy.octopus.octopusenergy.core.WebAuth
import energy.octopus.octopusenergy.core.logging.LogLevel
import energy.octopus.octopusenergy.core.logging.Logger
import energy.octopus.octopusenergy.core.model.AuthToken
import energy.octopus.octopusenergy.teslauth.TeslaAuthViewModel.Event.*
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
            redirectUri = "https://auth.tesla.com/void/callback?",
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
                }
                is Error -> onError(it.t)
                is AuthorizationCodeReceived -> {
                    onAuthorizationCodeReceived?.invoke(it.code)
                    if (onBearerTokenReceived != null) {
                        viewModel.getBearerToken(it.code)
                    }
                }
                Dismiss -> onDismiss()
            }
        }.launchIn(this)
    }
}
