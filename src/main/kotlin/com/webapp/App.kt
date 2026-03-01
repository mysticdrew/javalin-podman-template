package com.webapp

import com.webapp.config.AppConfig
import com.webapp.http.WebServer
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("com.webapp.App")

fun main() {
  val config = AppConfig.fromEnvironment()
  logger.info("Starting webapp on port={} env={}", config.port, config.environment)
  WebServer.create(config).start(config.port)
}
