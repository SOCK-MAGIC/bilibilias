package com.imcys.bilibilias.core.model

import kotlinx.serialization.Serializable

/**
 * User preferences.
 *
 * @property enableTryLook 免登录查看1080P视频
 */
@Serializable
data class UserPreferences(
    val selfInfo: SelfInfo?,
    val enableTryLook: Boolean,
    val setSubtitle: Boolean,
) {
    companion object {
        val DEFAULT = UserPreferences(
            selfInfo = null,
            enableTryLook = false,
            setSubtitle = true,
        )
    }
}