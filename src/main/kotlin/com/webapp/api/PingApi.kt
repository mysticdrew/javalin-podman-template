package com.webapp.api

import com.webapp.api.model.PingRequest
import io.javalin.http.Handler

class PingApi(private val environment: String, private val pingService: PingService) {
  fun ping(): Handler = Handler { ctx ->
    val request = ctx.bodyAsClass(PingRequest::class.java)
    val response = pingService.ping(request.message, environment)
    ctx.status(200).json(response)
  }
}
