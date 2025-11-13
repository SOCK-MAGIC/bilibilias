package com.imcys.bilibilias.core.datasource.ktor

import com.imcys.bilibilias.core.datasource.local.CredentialsDataSource
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url

internal class CookiesStorageImpl(
    private val credentials: CredentialsDataSource,
) : CookiesStorage {
    override suspend fun get(requestUrl: Url): List<Cookie> {
        return credentials.getCredentials().cookie.map {
            Cookie(it.key, it.value)
        }
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        credentials.updateCookies(mapOf(cookie.name to cookie.value))
    }

    override fun close() = Unit
}