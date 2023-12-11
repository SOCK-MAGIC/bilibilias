package com.imcys.network.configration

import androidx.collection.ArrayMap
import com.imcys.datastore.fastkv.ICookieStore
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.protobuf.ProtoBuf
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalSerializationApi::class)
class CookieManager
@Inject constructor(
    private val cookiesData: ICookieStore,
    private val cbor: Cbor,
    private val protoBuf: ProtoBuf
) : CookiesStorage {
    private val cache = ArrayMap<String, Cookie>(5)
    private val sterileMap = MapSerializer(String.serializer(), CookieSerializer)

    init {
        if (cookiesData.valid) {
            cookiesData.get()?.let { bytes ->
                cbor.decodeFromByteArray(sterileMap, bytes).map { (k, v) ->
                    cache.put(k, v)
                }
            }
        }
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        Timber.d("addCookie: $cookie")
        val timestamp = cookie.expires?.timestamp ?: return
        if (timestamp < System.currentTimeMillis()) {
            return
        }
        cache[cookie.name] = cookie
        cookiesData.setTimestamp(timestamp)
        save()
    }

    override suspend fun get(requestUrl: Url): List<Cookie> {
        Timber.d("getCookie: $requestUrl")
        val cookie = cache[SESSION_DATA]
        return if (cookie == null) {
            listOf()
        } else {
            listOf(cookie)
        }
    }

    override fun close() {
        save()
    }

    private fun save() {
        cookiesData.set(
            cbor.encodeToByteArray(
                sterileMap,
                cache
            )
        )
    }
}

const val SESSION_DATA = "SESSDATA"
