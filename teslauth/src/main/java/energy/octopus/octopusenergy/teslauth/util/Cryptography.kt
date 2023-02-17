package energy.octopus.octopusenergy.teslauth.util

import java.security.MessageDigest

internal fun String.sha256(): ByteArray {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(toByteArray(charset("UTF-8")))
}