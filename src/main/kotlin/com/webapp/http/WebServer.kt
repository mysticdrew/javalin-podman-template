package com.webapp.http

import com.webapp.api.PingApi
import com.webapp.api.PingService
import com.webapp.config.AppConfig
import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.http.HttpResponseException
import java.time.Clock
import java.util.UUID
import org.slf4j.LoggerFactory
import org.slf4j.MDC

object WebServer {
  private val logger = LoggerFactory.getLogger(WebServer::class.java)
  private const val REQUEST_ID_HEADER = "X-Request-Id"
  private const val REQUEST_ID_CONTEXT_KEY = "requestId"
  private const val REQUEST_START_NANOS = "requestStartNanos"

  fun create(config: AppConfig): Javalin {
    val pingApi = PingApi(config.environment, PingService(Clock.systemUTC()))
    return Javalin.create { javalinConfig ->
      javalinConfig.routes.before { ctx ->
        val requestId = requestIdFromHeader(ctx.header(REQUEST_ID_HEADER))
        ctx.attribute(REQUEST_ID_CONTEXT_KEY, requestId)
        ctx.attribute(REQUEST_START_NANOS, System.nanoTime())
        ctx.header(REQUEST_ID_HEADER, requestId)
        MDC.put(REQUEST_ID_CONTEXT_KEY, requestId)
      }

      javalinConfig.routes.after { ctx ->
        val startedAtNanos = ctx.attribute<Long>(REQUEST_START_NANOS) ?: 0L
        val elapsedMillis =
            if (startedAtNanos == 0L) 0L else (System.nanoTime() - startedAtNanos) / 1_000_000L
        logger.info(
            "request method={} path={} status={} durationMs={}",
            ctx.method(),
            ctx.path(),
            ctx.statusCode(),
            elapsedMillis,
        )
        MDC.clear()
      }

      javalinConfig.routes.exception(IllegalArgumentException::class.java) { ex, ctx ->
        ctx.status(400).json(ErrorResponse(ex.message ?: "bad request", requestId(ctx)))
      }

      javalinConfig.routes.exception(HttpResponseException::class.java) { ex, ctx ->
        val message = ex.message?.takeIf { it.isNotBlank() } ?: "request failed"
        ctx.status(ex.status).json(ErrorResponse(message, requestId(ctx)))
      }

      javalinConfig.routes.exception(Exception::class.java) { ex, ctx ->
        logger.error("Unhandled error while processing request", ex)
        ctx.status(500).json(ErrorResponse("internal server error", requestId(ctx)))
      }

      javalinConfig.routes.get("/health") { ctx ->
        ctx.json(mapOf("status" to "ok", "environment" to config.environment))
      }
      javalinConfig.routes.post("/api/v1/ping", pingApi.ping())
    }
  }

  private fun requestIdFromHeader(requestIdHeader: String?): String {
    return if (requestIdHeader.isNullOrBlank()) UUID.randomUUID().toString() else requestIdHeader
  }

  private fun requestId(ctx: Context): String {
    return ctx.attribute<String>(REQUEST_ID_CONTEXT_KEY) ?: "unknown"
  }
}
