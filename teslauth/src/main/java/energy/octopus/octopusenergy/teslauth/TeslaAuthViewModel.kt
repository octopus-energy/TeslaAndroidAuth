package energy.octopus.octopusenergy.teslauth

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import energy.octopus.octopusenergy.teslauth.api.TeslaApi
import energy.octopus.octopusenergy.teslauth.model.AccessTokenRequest
import energy.octopus.octopusenergy.teslauth.model.AuthToken
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
            .appendQueryParameter("client_id", CLIENT_ID)
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

    fun onAuthorizationCodeReceived(code: String?) {
        code ?: return
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val bearerTokenResponse = api.exchangeAuthCodeForBearerToken(
                    BearerTokenRequest(
                        grantType = "authorization_code",
                        clientId = CLIENT_ID,
                        code = code,
                        codeVerifier = codeChallenge.verifier,
                        redirectUri = REDIRECT_URL,
                    )
                )
                Log.i(TESLA_AUTH_TAG, "Got bearer token $bearerTokenResponse")
                val authTokenResponse = api.exchangeBearerTokenForAccessToken(
                    bearerTokenResponse.accessToken, AccessTokenRequest(
                        grantType = "urn:ietf:params:oauth:grant-type:jwt-bearer",
                        clientId = "81527cff06843c8634fdc09e8ac0abefb46ac849f38fe1e431c2ef2106796384",
                        clientSecret = "c7257eb71a564034f9419ee651c7d0e5f7aa6bfbd18bafb5c5c033b093bb2fa3"
                    )
                )
                Log.i(TESLA_AUTH_TAG, "Got auth token $authTokenResponse")
                _event.emit(
                    Event.Success(
                        AuthToken(
                            accessToken = authTokenResponse.accessToken,
                            refreshToken = authTokenResponse.refreshToken,
                            expiresIn = authTokenResponse.expiresIn,
                            createdAt = authTokenResponse.createdAt
                                ?: throw IllegalStateException("Created at is null")
                        )
                    )
                )
            } catch (t: Throwable) {
                _event.emit(Event.Error(t))
            }
        }
    }

    fun onPageLoaded() {
        _isLoading.value = false
    }

    sealed class Event {
        data class Error(val t: Throwable) : Event()
        data class Success(val token: AuthToken) : Event()
    }
}