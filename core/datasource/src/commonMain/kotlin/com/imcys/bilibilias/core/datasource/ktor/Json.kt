package com.imcys.bilibilias.core.datasource.ktor

import kotlinx.serialization.json.Json

val HttpClientJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}