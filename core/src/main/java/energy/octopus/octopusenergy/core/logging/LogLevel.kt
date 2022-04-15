package energy.octopus.octopusenergy.core.logging


/**
 *  Supported Log levels
 *  {@link #DEFAULT}
 *  {@link #EMPTY}
 */
enum class LogLevel {
    /**
     * [DEFAULT] provides logs over the main operation steps, such as generated url, auth token, etc...
     * useful for debugging purposes
     */
    DEFAULT,
    /**
     * [EMPTY] provides no logs, useful for production builds
     */
    EMPTY
}