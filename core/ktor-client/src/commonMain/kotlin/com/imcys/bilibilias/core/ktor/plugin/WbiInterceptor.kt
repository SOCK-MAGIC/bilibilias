package com.imcys.bilibilias.core.ktor.plugin

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.DelicateCryptographyApi
import dev.whyoleg.cryptography.algorithms.MD5
import io.ktor.http.encodeURLParameter
import kotlin.time.Clock

internal object WbiAuthenticator {
    private val mixinKeyEncTab = intArrayOf(
        46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
        33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
        61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
        36, 20, 34, 44, 52
    )
    private const val WTS_KEY = "wts"
    private const val WRID_KEY = "w_rid"
    fun getMixinKey(imgKey: String, subKey: String): String {
        val source = imgKey + subKey
        return buildString(64) {
            mixinKeyEncTab.forEach { index ->
                append(source[index])
            }
        }
    }

    fun signParams(params: Map<String, Any>, wbiKey: String): String {
        val sortedMap = params.toSortedMap()

        sortedMap.getOrPut(WTS_KEY) { Clock.System.now().toEpochMilliseconds() / 1000 }

        val originalQueryString = sortedMap.entries.joinToString("&") { (k, v) ->
            "${k.encodeURLParameter()}=${v.toString().encodeURLParameter()}"
        }
        val wRid = (originalQueryString + wbiKey).toMD5()

        return "$originalQueryString&$WRID_KEY=$wRid"
    }

    @OptIn(DelicateCryptographyApi::class)
    private fun String.toMD5(): String {
        return CryptographyProvider.Default
            .get(MD5)
            .hasher()
            .hashBlocking(this.toByteArray())
            .toHexString()
    }
}