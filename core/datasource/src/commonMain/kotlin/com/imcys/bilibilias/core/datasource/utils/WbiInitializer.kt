package com.imcys.bilibilias.core.datasource.utils

import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.logging.logger
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

// TODO: 添加一个记录方法，只在每天更新，避免重复请求
class WbiInitializer(
    private val api: BilibiliApi,
) {
    private val logger = logger<WbiInitializer>()
    suspend fun initialize() {
        var attempt = 0
        while (attempt < 5)
            try {
                val data = api.getNavigationData()
                val wbiImg = data.wbiImg
                WbiSign.initializeMixinKey(wbiImg.imgUrl, wbiImg.subUrl)
                logger.info { "WBI initialized successfully." }
                break
            } catch (e: Exception) {
                attempt++
                delay(1.seconds)
                logger.error(e) { "Error initializing WBI (attempt $attempt)." }
            }
    }
}