package energy.octopus.octopusenergy.teslauth.logging

import android.util.Log

object Logger {

    private const val TESLA_AUTH_TAG = "Tesla_Auth"

    var level: LogLevel = LogLevel.EMPTY

    fun log(message: String) {
        when (level) {
            LogLevel.DEFAULT -> Log.i(TESLA_AUTH_TAG, message)
            LogLevel.EMPTY -> {}
        }
    }
}