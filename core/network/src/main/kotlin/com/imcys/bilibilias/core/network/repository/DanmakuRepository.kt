package com.imcys.bilibilias.core.network.repository

import com.imcys.bilibilias.core.network.api.BilibiliApi
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsChannel
import io.ktor.util.Deflate
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray
import javax.inject.Inject

class DanmakuRepository @Inject constructor(
    private val client: HttpClient,
) {
    suspend fun getRealTimeDanmaku(cid: Long): ByteArray {
        val response = client.get(BilibiliApi.DM_REAL_TIME) {
            parameter("oid", cid)
        }.bodyAsChannel()
        val channel = with(Deflate) {
            decode(response)
        }
        return channel.readRemaining().readByteArray()
    }
}
