package com.webapp.api

import com.webapp.api.model.PingResponse
import java.time.Clock

class PingService(private val clock: Clock) {
  fun ping(message: String?, environment: String): PingResponse {
    val validatedMessage = validateMessage(message)
    return PingResponse(validatedMessage, environment, clock.instant().toString())
  }

  companion object {
    private const val MAX_MESSAGE_LENGTH = 120

    fun validateMessage(message: String?): String {
      require(!message.isNullOrBlank()) { "message must not be blank" }
      require(message.length <= MAX_MESSAGE_LENGTH) { "message must be 120 characters or fewer" }
      return message
    }
  }
}
