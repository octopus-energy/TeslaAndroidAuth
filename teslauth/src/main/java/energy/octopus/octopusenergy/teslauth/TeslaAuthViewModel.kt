package energy.octopus.octopusenergy.teslauth

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import energy.octopus.octopusenergy.core.model.AuthToken
import energy.octopus.octopusenergy.teslauth.TeslaAuthViewModel.Event.*
import energy.octopus.octopusenergy.teslauth.api.TeslaApi
import energy.octopus.octopusenergy.teslauth.model.BearerTokenRequest
import energy.octopus.octopusenergy.teslauth.util.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class TeslaAuthViewModel(private val api: TeslaApi = TeslaApi()) : ViewModel() {

    private val codeChallenge = CodeChallenge()
    val url = Uri.Builder().apply {
        this.scheme("https")
            .authority("auth.tesla.com")
            .appendPath("oauth2")
            .appendPath("v3")
            .appendPath("authorize")
            .appendQueryParameter("client_id", WEB_CLIENT_ID)
            .appendQueryParameter("code_challenge", codeChallenge.challenge)
            .appendQueryParameter("code_challenge_method", codeChallenge.method)
            .appendQueryParameter("redirect_uri", REDIRECT_URL)
            .appendQueryParameter("response_type", RESPONSE_TYPE)
            .appendQueryParameter("scope", SCOPE)
            .appendQueryParameter("state", STATE)
    }.build().toString()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _event = MutableSharedFlow<Event>()
    val event: SharedFlow<Event> = _event

    fun getBearerToken(code: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val bearerTokenResponse = api.exchangeAuthCodeForBearerToken(
                    BearerTokenRequest(
                        grantType = "authorization_code",
                        clientId = WEB_CLIENT_ID,
                        code = code,
                        codeVerifier = codeChallenge.verifier,
                        redirectUri = REDIRECT_URL,
                    )
                )
                energy.octopus.octopusenergy.core.logging.Logger.log("Got bearer token $bearerTokenResponse")
                _event.emit(
                    ReceivedBearerToken(
                        AuthToken(
                            accessToken = bearerTokenResponse.accessToken,
                            refreshToken = bearerTokenResponse.refreshToken,
                            expiresIn = bearerTokenResponse.expiresIn,
                            createdAt = bearerTokenResponse.createdAt
                        )
                    )
                )
            } catch (t: Throwable) {
                _event.emit(Error(t))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onCodeReceived(code: String?) {
        code ?: return

        viewModelScope.launch {
            val error = code.substringAfter("error", missingDelimiterValue = "")
            _event.emit(
                if (error.isNotBlank()) {
                    if (error.contains("cancelled", ignoreCase = true)) Dismiss
                    else Error(IllegalStateException("Auth failed with: $error"))
                } else AuthorizationCodeReceived(code)
            )
        }
    }


    fun onPageLoaded() {
        _isLoading.value = false
    }

    sealed class Event {
        object Dismiss : Event()
        data class Error(val t: Throwable) : Event()
        data class AuthorizationCodeReceived(val code: String) : Event()
        data class ReceivedBearerToken(val token: AuthToken) : Event()
    }
}