package com.imcys.bilibilias.core.ktor.plugin;

import com.imcys.bilibilias.core.datastore.WbiKeyDataStore
import com.imcys.bilibilias.core.datastore.model.WbiKeysData
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.http.parseQueryString
import io.ktor.util.AttributeKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

val isWbi = AttributeKey<Boolean>("isWbi")
val client = HttpClient {
    install(WbiPlugin) {
//        wbiKeyDataStore = WbiKeyDataStore()
    }
}

class WbiPlugin(
    private val wbiKeyDataStore: WbiKeyDataStore,
    private val result: () -> WbiKeysData,
) {
    private var cachedKeys: WbiKeysData? = null
    private val mutex = Mutex()

    suspend fun getValidKeys(): WbiKeysData {
        // 1. 尝试从内存缓存获取，如果有效则直接返回 (最快)
        cachedKeys?.let { if (!it.isExpired()) return it }

        // 2. 如果内存缓存无效，则进入同步代码块，防止并发刷新
        return mutex.withLock {
            // 3. 双重检查：在等待锁的时候，可能其他线程已经刷新了缓存
            cachedKeys?.let { if (!it.isExpired()) return@withLock it }

            // 4. 尝试从磁盘加载，如果有效，则更新缓存并返回
            val keysFromDisk = wbiKeyDataStore.wbiKeysData.firstOrNull()
            if (keysFromDisk != null && !keysFromDisk.isExpired()) {
                cachedKeys = keysFromDisk
                return@withLock keysFromDisk
            }

            // 5. 如果磁盘数据也无效，则从网络获取
            println("WBI keys expired or missing. Fetching new keys from network...")
            val newKeys = TODO()

            // 6. 更新内存缓存
            cachedKeys = newKeys

            wbiKeyDataStore.updateKeys("", "")
            // 8. 返回新的密钥
            return@withLock newKeys
        }
    }

    class Config {
        var wbiKeyDataStore: WbiKeyDataStore? = null
        var fallback: (() -> WbiKeysData)? = null
    }

    companion object Plugin : HttpClientPlugin<Config, WbiPlugin> {
        override val key: AttributeKey<WbiPlugin> = AttributeKey("WbiPlugin")
        override fun prepare(block: Config.() -> Unit): WbiPlugin {
            val config = Config().apply(block)
            return WbiPlugin(config.wbiKeyDataStore!!, config.fallback!!)
        }

        override fun install(plugin: WbiPlugin, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Render) {
                if (context.attributes.getOrNull(isWbi) != true) {
                    proceed()
                    return@intercept
                }

                val keysData = plugin.wbiKeyDataStore.wbiKeysData.first()
                if (!keysData.isExpired()) {
                    val mixinKey = WbiAuthenticator.getMixinKey(keysData.imgKey, keysData.subKey)

                    val originalParams = context.url.parameters.entries()
                        .associate { it.key to it.value.first() }
                        .toSortedMap()
                    val enc = WbiAuthenticator.signParams(originalParams, mixinKey)
                    context.url.encodedParameters.clear()
                    context.url.encodedParameters.appendAll(parseQueryString(enc))
                    proceed()
                } else {
                }
                proceed()
            }
        }
    }
}
