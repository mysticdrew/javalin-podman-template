plugins {
  application
  jacoco
  kotlin("jvm") version "2.2.20"
  id("com.diffplug.spotless") version "8.3.0"
}

group = "com.webapp"

version = "1.0.0"

repositories { mavenCentral() }

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib")
  implementation("io.javalin:javalin:7.0.1")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.20.1")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.20.1")
  runtimeOnly("ch.qos.logback:logback-classic:1.5.18")

  testImplementation(platform("org.junit:junit-bom:5.12.2"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin { jvmToolchain(21) }

application { mainClass.set("com.webapp.AppKt") }

tasks.test {
  useJUnitPlatform()
  finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports {
    xml.required.set(true)
    html.required.set(true)
  }
}

spotless {
  kotlin {
    target("src/*/kotlin/**/*.kt")
    ktfmt()
  }
  kotlinGradle {
    target("*.gradle.kts")
    ktfmt()
  }
}
