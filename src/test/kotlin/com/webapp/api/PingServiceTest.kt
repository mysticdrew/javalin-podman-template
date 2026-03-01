package com.webapp.api

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class PingServiceTest {
  @Test
  fun pingReturnsExpectedResponse() {
    val fixedClock = Clock.fixed(Instant.parse("2026-03-01T12:00:00Z"), ZoneOffset.UTC)
    val service = PingService(fixedClock)

    val response = service.ping("hello", "test")

    assertEquals("hello", response.message)
    assertEquals("test", response.environment)
    assertEquals("2026-03-01T12:00:00Z", response.timestamp)
  }

  @Test
  fun pingRejectsBlankMessage() {
    val service = PingService(Clock.systemUTC())
    assertThrows(IllegalArgumentException::class.java) { service.ping("   ", "test") }
  }

  @Test
  fun pingRejectsOverlongMessage() {
    val service = PingService(Clock.systemUTC())
    val longMessage = "x".repeat(121)
    assertThrows(IllegalArgumentException::class.java) { service.ping(longMessage, "test") }
  }
}
