package com.imcys.bilibilias.core.domain

import io.ktor.client.HttpClient
import io.ktor.client.request.head
import io.ktor.http.HttpHeaders

interface RedirectResolverUseCase {
    suspend fun resolveUrl(shortUrl: String): String
}

class BilibiliRedirectResolverUseCase(
    private val httpClient: HttpClient
) : RedirectResolverUseCase {
    override suspend fun resolveUrl(shortUrl: String): String {
        val response = httpClient.head(shortUrl)
        val longUrl = response.headers[HttpHeaders.Location]
            ?: throw IllegalStateException("No location header in redirect response for $shortUrl")

        return longUrl
    }
}