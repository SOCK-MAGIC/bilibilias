package com.imcys.bilibilias.core.datastore.model

import kotlinx.serialization.Serializable

@Serializable
data class Users(
    val id: Long = 0,
    val isLogin: Boolean = false,
    val mixKey: String? = null,
)
