package energy.octopus.octopusenergy.teslauth.model

internal data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val createdAt: Long?
)