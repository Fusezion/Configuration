package dev.lyric.configuration.exception

class ConfigException(message: String, cause: Throwable? = null) : RuntimeException(message, cause) {
}