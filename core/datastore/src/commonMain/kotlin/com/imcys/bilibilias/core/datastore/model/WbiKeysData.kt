package com.imcys.bilibilias.core.datastore.model

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

@Serializable
data class WbiKeysData(
    val imgKey: String = "",
    val subKey: String = "",
    val fetchedAt: Instant = Instant.fromEpochMilliseconds(0)
) {
    /**
     * 检查当前缓存的 WBI keys 是否已过期或无效。
     *
     * B站的 WBI keys 刷新频率较高，我们设置一个保守的缓存有效期。
     *
     * @param validityDuration 缓存的有效时长，默认为10分钟。
     * @return 如果 keys 无效 (为空) 或已过期，则返回 true。
     */
    fun isExpired(validityDuration: Duration = 10.minutes): Boolean {
        if (imgKey.isBlank() || subKey.isBlank()) {
            return true
        }

        if (fetchedAt == Instant.fromEpochMilliseconds(0)) {
            return true
        }
        val now = Clock.System.now()
        val elapsedTime: Duration = now - fetchedAt

        return elapsedTime > validityDuration
    }
}