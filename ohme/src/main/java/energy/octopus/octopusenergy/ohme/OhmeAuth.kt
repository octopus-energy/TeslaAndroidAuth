package energy.octopus.octopusenergy.ohme

import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import energy.octopus.octopusenergy.core.OctopusWebSettings
import energy.octopus.octopusenergy.core.WebAuth
import energy.octopus.octopusenergy.core.logging.LogLevel
import energy.octopus.octopusenergy.core.logging.Logger

typealias OnAuthorizationCodeReceivedCallback = (String) -> Unit

/**
 *  A Composable that shows an embedded [WebView] for Ohme Authentication
 *  @param modifier Modifier to be applied to the button
 *  @param isLoggingEnabled whether Logging is enabled or not
 *  @param onAuthorizationCodeReceived callback called with the code represented by a [String] as parameter if getting the authorization code was successful
 *  @param onError callback called with the [Throwable] that occurred when trying to get the authorization token
 *  @param onDismiss called when the underlying [WebView] can't go back and the back press represents a dismiss of the Authentication WebView
 *  @param loadingIndicator optional composable, default is [CircularProgressIndicator]
 */

@Composable
fun OhmeAuth(
    clientId: String,
    redirectUri: String,
    state: String,
    modifier: Modifier = Modifier,
    isLoggingEnabled: Boolean = false,
    isDebug: Boolean = false,
    onAuthorizationCodeReceived: OnAuthorizationCodeReceivedCallback? = null,
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
    Logger.level = if (isLoggingEnabled) LogLevel.DEFAULT else LogLevel.EMPTY
    var isLoading by remember { mutableStateOf(true) }


    Box(modifier.fillMaxSize()) {
        WebAuth(
            url = getUrl(isDebug, clientId, redirectUri, state),
            redirectUri = redirectUri,
            modifier = modifier.fillMaxSize(),
            octopusWebSettings = OctopusWebSettings(
                javaScriptEnabled = true,
                domStorageEnabled = true
            ),
            onCodeReceived = { code ->
                code?.let {
                    onAuthorizationCodeReceived?.invoke(it)
                } ?: onError(IllegalStateException("The received code is null"))
            },
            onPageLoaded = {
                isLoading = false
            },
            onDismiss = onDismiss,
        )
        if (isLoading) {
            loadingIndicator()
        }
    }
}

private const val debugHost = "api-dev.ohme.io"
private const val liveHost = "api.ohme.io"

private fun getUrl(isDebug: Boolean, clientId: String, redirectUri: String, state: String) =
    "https://${if (isDebug) debugHost else liveHost}/#/authentication/login?&response_type=code&client_id=$clientId&redirect_uri=$redirectUri&scope=app&state=$state"
