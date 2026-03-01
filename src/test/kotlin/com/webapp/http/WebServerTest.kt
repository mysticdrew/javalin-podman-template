package com.webapp.http

import com.webapp.config.AppConfig
import io.javalin.Javalin
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class WebServerTest {
  @Test
  @Throws(IOException::class, InterruptedException::class)
  fun healthEndpointReturnsOk() {
    val app = WebServer.create(AppConfig(0, "test")).start(0)
    try {
      val response = sendRequest(app, "/health", "GET", null, null)
      assertEquals(200, response.statusCode(), response.body())
      assertTrue(response.body().contains("\"status\":\"ok\""))
      assertTrue(response.body().contains("\"environment\":\"test\""))
    } finally {
      app.stop()
    }
  }

  @Test
  @Throws(IOException::class, InterruptedException::class)
  fun pingEndpointReturnsPayload() {
    val app = WebServer.create(AppConfig(0, "test")).start(0)
    try {
      val response = sendRequest(app, "/api/v1/ping", "POST", "{\"message\":\"hello\"}", null)
      assertEquals(200, response.statusCode(), response.body())
      assertTrue(response.body().contains("\"message\":\"hello\""))
      assertTrue(response.body().contains("\"environment\":\"test\""))
      assertTrue(response.body().contains("\"timestamp\""))
    } finally {
      app.stop()
    }
  }

  @Test
  @Throws(IOException::class, InterruptedException::class)
  fun pingEndpointValidatesMessage() {
    val app = WebServer.create(AppConfig(0, "test")).start(0)
    try {
      val response = sendRequest(app, "/api/v1/ping", "POST", "{\"message\":\"\"}", null)
      assertEquals(400, response.statusCode(), response.body())
      assertTrue(response.body().contains("\"error\""))
      assertTrue(response.body().contains("\"requestId\""))
    } finally {
      app.stop()
    }
  }

  @Test
  @Throws(IOException::class, InterruptedException::class)
  fun requestIdHeaderIsPropagated() {
    val app = WebServer.create(AppConfig(0, "test")).start(0)
    try {
      val response = sendRequest(app, "/health", "GET", null, "req-123")
      assertEquals(200, response.statusCode(), response.body())
      assertEquals("req-123", response.headers().firstValue("X-Request-Id").orElse(null))
    } finally {
      app.stop()
    }
  }

  @Throws(IOException::class, InterruptedException::class)
  private fun sendRequest(
      app: Javalin,
      path: String,
      method: String,
      jsonBody: String?,
      requestId: String?,
  ): HttpResponse<String> {
    val client = HttpClient.newHttpClient()
    val baseUrl = "http://localhost:" + app.port()
    val builder = HttpRequest.newBuilder(URI.create(baseUrl + path))

    if (requestId != null) {
      builder.header("X-Request-Id", requestId)
    }

    if (method == "POST") {
      builder.header("Content-Type", "application/json")
      builder.POST(HttpRequest.BodyPublishers.ofString(jsonBody ?: ""))
    } else {
      builder.GET()
    }

    return client.send(builder.build(), HttpResponse.BodyHandlers.ofString())
  }
}
