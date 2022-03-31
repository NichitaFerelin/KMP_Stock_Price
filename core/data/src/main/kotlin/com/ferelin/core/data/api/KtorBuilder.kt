package com.ferelin.core.data.api

import com.ferelin.core.data.BuildConfig
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*

internal fun buildKtorHttpClient(): HttpClient {
  return HttpClient(Android) {
    engine {
      connectTimeout = 60_000
    }
    install(JsonFeature) {
      serializer = KotlinxSerializer(
        json = kotlinx.serialization.json.Json {
          ignoreUnknownKeys = true
        }
      )
    }
    if (BuildConfig.DEBUG) {
      install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.ALL
      }
    }
  }
}