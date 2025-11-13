package com.imcys.bilibilias.core.datasource.ktor

import com.imcys.bilibilias.core.logging.logger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.util.AttributeKey
import kotlin.jvm.kotlin

internal fun HttpClientConfig<*>.HttpLogging() {
    val httpLogger = KotlinLogging.logger("BilibiliApi")
    Logging {
        level = LogLevel.BODY
        logger = object : Logger {
            override fun log(message: String) {
                httpLogger.info { message }
            }
        }
        filter { request ->
            request.attributes.getOrNull(DisableLogging) != true
        }
    }
}

internal val DisableLogging = AttributeKey<Boolean>("DisableLogging")