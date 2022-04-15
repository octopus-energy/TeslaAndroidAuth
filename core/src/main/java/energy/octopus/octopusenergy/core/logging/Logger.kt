package energy.octopus.octopusenergy.core.logging

import android.util.Log

object Logger {

    private const val AUTH_TAG = "Octopus_Energy_Auth"

    var level: LogLevel = LogLevel.EMPTY

    fun log(message: String) {
        when (level) {
            LogLevel.DEFAULT -> Log.i(AUTH_TAG, message)
            LogLevel.EMPTY -> {}
        }
    }
}