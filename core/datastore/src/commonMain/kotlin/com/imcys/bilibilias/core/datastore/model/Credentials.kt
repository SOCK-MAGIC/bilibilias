package com.imcys.bilibilias.core.datastore.model

data class Credentials(
    val cookie: Map<String, String> = emptyMap(),
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val expiresAt: Long? = null,
    val mid: Long? = null, // 用户ID
)