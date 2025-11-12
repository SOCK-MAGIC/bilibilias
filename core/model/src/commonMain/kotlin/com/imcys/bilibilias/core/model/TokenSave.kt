package com.imcys.bilibilias.core.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenSave(
    val refreshToken: String? = null,
) {
    companion object {
        val INIT = TokenSave()
    }
}