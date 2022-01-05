package energy.octopus.octopusenergy.teslauth.util

import android.util.Base64
import android.util.Base64.NO_PADDING
import android.util.Base64.URL_SAFE
import org.apache.commons.lang3.RandomStringUtils

internal class CodeChallenge {
    val verifier = RandomStringUtils.randomAlphabetic(86)
    val challenge = Base64.encodeToString(verifier.toByteArray(), NO_PADDING or URL_SAFE)
    val method = "S256"
}