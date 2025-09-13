package com.imcys.bilibilias.core.datasource.json

import kotlinx.serialization.json.Json

val HttpClientJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}