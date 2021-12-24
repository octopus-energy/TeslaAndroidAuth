package energy.octopus.octopusenergy.teslauth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class BearerTokenRequest(
    @SerialName("grant_type")
    val grantType: String,
    @SerialName("client_id")
    val clientId: String,
    @SerialName("code")
    val code: String,
    @SerialName("code_verifier")
    val codeVerifier: String,
    @SerialName("redirect_uri")
    val redirectUri: String,
)