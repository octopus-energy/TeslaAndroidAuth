package energy.octopus.octopusenergy.teslauth.api

import energy.octopus.octopusenergy.teslauth.logging.LogLevel.*
import energy.octopus.octopusenergy.teslauth.model.AccessTokenRequest
import energy.octopus.octopusenergy.teslauth.model.BearerTokenRequest
import energy.octopus.octopusenergy.teslauth.model.TokenResponse
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import energy.octopus.octopusenergy.teslauth.logging.Logger as TeslaAuthLogger

internal class TeslaApi {

    private val client = HttpClient {
        if (TeslaAuthLogger.level == DEFAULT) {
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
        }
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private fun HttpRequestBuilder.json() {
        contentType(ContentType.Application.Json)
    }

    suspend fun exchangeAuthCodeForBearerToken(request: BearerTokenRequest): TokenResponse =
        client.post {
            url {
                takeFrom("https://auth.tesla.com")
                encodedPath = "oauth2/v3/token"
            }
            json()
            body = request
        }

    suspend fun exchangeBearerTokenForAccessToken(
        bearerToken: String,
        request: AccessTokenRequest
    ): TokenResponse =
        client.post {
            url {
                takeFrom("https://owner-api.teslamotors.com")
                encodedPath = "oauth/token"
            }
            headers {
                append(HttpHeaders.Authorization, "Bearer $bearerToken")
            }
            json()
            body = request
        }
}