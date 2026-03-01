package com.webapp.config

data class AppConfig(val port: Int, val environment: String) {
  companion object {
    private const val DEFAULT_PORT = 7000
    private const val DEFAULT_ENVIRONMENT = "dev"

    fun fromEnvironment(): AppConfig {
      val environment = valueOrDefault(System.getenv("APP_ENV"), DEFAULT_ENVIRONMENT)
      val port = parsePort(System.getenv("PORT"))
      return AppConfig(port, environment)
    }

    private fun valueOrDefault(value: String?, fallback: String): String {
      return if (value.isNullOrBlank()) fallback else value
    }

    private fun parsePort(rawPort: String?): Int {
      if (rawPort.isNullOrBlank()) {
        return DEFAULT_PORT
      }

      return rawPort.toIntOrNull()?.also {
        require(it in 1..65535) { "PORT must be between 1 and 65535." }
      } ?: throw IllegalArgumentException("PORT must be a valid integer.")
    }
  }
}
