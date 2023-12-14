package com.imcys.network.repository.danmaku

import com.imcys.bilibilias.dm.DmSegMobileReply

interface IDanmakuDataSources {
    suspend fun xml(cid: Long): ByteArray

    @Deprecated("网页端接口大多需要wbi")
    suspend fun proto(cid: Long, index: Int, type: Int = 1): DmSegMobileReply
    suspend fun protoWbi(cid: Long, index: Int, type: Int = 1): DmSegMobileReply
}
