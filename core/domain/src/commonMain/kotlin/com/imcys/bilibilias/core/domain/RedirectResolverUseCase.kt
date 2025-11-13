package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.datasource.api.BilibiliApi

interface RedirectResolverUseCase {
    suspend fun resolveUrl(shortUrl: String): String
}

class BilibiliRedirectResolverUseCase(
    private val api: BilibiliApi
) : RedirectResolverUseCase {
    override suspend fun resolveUrl(shortUrl: String): String {
        return api.resolve(shortUrl)
    }
}