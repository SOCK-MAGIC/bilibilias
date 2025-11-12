package com.imcys.bilibilias.core.datasource.di

import androidx.datastore.core.DataStoreFactory
import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.datasource.api.BilibiliLoginApi
import com.imcys.bilibilias.core.datasource.ktor.ApiResponseUnwrapper
import com.imcys.bilibilias.core.datasource.ktor.CookiesStorageImpl
import com.imcys.bilibilias.core.datasource.ktor.HttpClientJson
import com.imcys.bilibilias.core.datasource.ktor.HttpLogging
import com.imcys.bilibilias.core.datasource.local.AsPreferencesDataSource
import com.imcys.bilibilias.core.datasource.local.CookieJarDataSource
import com.imcys.bilibilias.core.datasource.local.DataStoreMediaCacheDataSource
import com.imcys.bilibilias.core.datasource.local.MediaCacheDataSource
import com.imcys.bilibilias.core.datasource.utils.WbiInitializer
import com.imcys.bilibilias.core.datastore.ReplaceFileCorruptionHandler
import com.imcys.bilibilias.core.datastore.asDataStoreSerializer
import com.imcys.bilibilias.core.datastore.new
import com.imcys.bilibilias.core.datastore.resolveDataStoreFile
import com.imcys.bilibilias.core.di.applicationScope
import com.imcys.bilibilias.core.ktor.client.createHttpClient
import com.imcys.bilibilias.core.model.MediaCacheSave
import com.imcys.bilibilias.core.model.UserPreferences
import com.imcys.bilibilias.core.platform.AppDirs
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.serialization.kotlinx.protobuf.protobuf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val DataSourceModule = module {
    single {
        BilibiliLoginApi(
            createHttpClient {
                defaultRequest {
                    url("https://passport.bilibili.com")
                }
                install(ApiResponseUnwrapper)
                install(ContentNegotiation) {
                    json(HttpClientJson)
                }
                install(HttpCookies) {
                    storage = get()
                }
                BrowserUserAgent()
                HttpLogging()
            },
            get(),
        )
    }
    single {
        BilibiliApi(
            createHttpClient {
                defaultRequest {
                    url("https://api.bilibili.com")
                    header(HttpHeaders.Origin, "https://m.bilibili.com")
                    header(HttpHeaders.Referrer, "https://m.bilibili.com")
                }
                install(HttpCookies) {
                    storage = get<CookiesStorage>()
                }
                install(ApiResponseUnwrapper)
                install(ContentNegotiation) {
                    json(HttpClientJson)
                    protobuf(contentType = ContentType.Application.OctetStream)
                }
                BrowserUserAgent()
                HttpLogging()
            },
            get(),
            get(),
        )
    }
    factoryOf(::WbiInitializer)
    singleOf(::CookiesStorageImpl) bind CookiesStorage::class
    single<MediaCacheDataSource> {
        DataStoreMediaCacheDataSource(
            store = DataStoreFactory.new(
                serializer = ListSerializer(MediaCacheSave.serializer()).asDataStoreSerializer { emptyList() },
                corruptionHandler = ReplaceFileCorruptionHandler { emptyList() },
                produceFile = {
                    resolveDataStoreFile(get<AppDirs>().dataStoreDir, "media_cache_storage")
                },
                scope = CoroutineScope(applicationScope.coroutineContext + Dispatchers.IO),
            )
        )
    }
    single {
        AsPreferencesDataSource(
            DataStoreFactory.new(
                serializer = UserPreferences.serializer()
                    .asDataStoreSerializer { UserPreferences.DEFAULT },
                corruptionHandler = ReplaceFileCorruptionHandler { UserPreferences.DEFAULT },
                produceFile = {
                    resolveDataStoreFile(
                        get<AppDirs>().dataStoreDir,
                        "user_preferences"
                    )
                },
                scope = CoroutineScope(applicationScope.coroutineContext + Dispatchers.IO),
            ),
        )
    }
    single {
        CookieJarDataSource(
            DataStoreFactory.new(
                serializer = MapSerializer(String.serializer(), String.serializer())
                    .asDataStoreSerializer { emptyMap() },
                corruptionHandler = ReplaceFileCorruptionHandler { emptyMap() },
                produceFile = { resolveDataStoreFile(get<AppDirs>().dataStoreDir, "cookie_jar") },
                scope = CoroutineScope(applicationScope.coroutineContext + Dispatchers.IO),
            ),
        )
    }
}