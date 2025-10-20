package com.imcys.bilibilias.core.datasource.ktor

import com.imcys.bilibilias.core.logging.logger
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.util.AttributeKey

internal fun HttpClientConfig<*>.HttpLogging() {
    val httpLogger = logger("BilibiliApi")
    Logging {
        level = LogLevel.BODY
        logger = object : Logger {
            override fun log(message: String) {
                httpLogger.info { message }
            }
        }
        filter { request ->
            request.attributes.getOrNull(DisableLogging) == true
        }
    }
}

val DisableLogging = AttributeKey<Boolean>("ss")