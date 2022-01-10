package energy.octopus.octopusenergy.teslauth.model

/**
 * Holds information about the authorization token from
 * https://tesla-api.timdorr.com/api-basics/authentication#post-https-owner-api.teslamotors.com-oauth-token
 * @property accessToken the SSO authorization token
 * @property refreshToken the SSO refresh token
 * @property expiresIn expiration timestamp of the token
 * @property createdAt timestamp of creation
 */
data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val createdAt: Long?
)