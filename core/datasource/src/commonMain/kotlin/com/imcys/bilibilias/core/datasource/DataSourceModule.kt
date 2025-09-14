package com.imcys.bilibilias.core.datasource

import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.datasource.api.BilibiliLoginApi
import com.imcys.bilibilias.core.datasource.json.HttpClientJson
import com.imcys.bilibilias.core.datasource.persistent.CookiesStorageImpl
import com.imcys.bilibilias.core.datasource.utils.ApiResponseUnwrapper
import com.imcys.bilibilias.core.datasource.utils.WbiInitializer
import com.imcys.bilibilias.core.ktor.client.createHttpClient
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
}