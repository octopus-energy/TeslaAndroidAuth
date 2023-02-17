package energy.octopus.octopusenergy.teslauth.util

import android.util.Base64
import android.util.Base64.NO_PADDING
import android.util.Base64.NO_WRAP
import android.util.Base64.URL_SAFE
import org.apache.commons.lang3.RandomStringUtils

internal class CodeChallenge {
    val verifier: String = RandomStringUtils.randomAlphabetic(86)
    val challenge: String = Base64.encodeToString(verifier.sha256(), NO_PADDING or NO_WRAP or URL_SAFE)
    val method = "S256"
}