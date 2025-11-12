package com.imcys.bilibilias.core.datasource.local

import androidx.datastore.core.DataStore
import com.imcys.bilibilias.core.model.TokenSave
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class CookieJarDataSource(
    private val cookieDataStore: DataStore<Map<String, String>>,
) {
    val cookies = cookieDataStore.data
    suspend fun add(name: String, value: String) {
        cookieDataStore.updateData { currentCookies ->
            currentCookies + (name to value)
        }
    }

    suspend fun getCookie(name: String): String? {
        return cookieDataStore.data.first()[name]
    }

    suspend fun removeCookie(name: String) {
        cookieDataStore.updateData { currentCookies ->
            currentCookies - name
        }
    }

    suspend fun clearCookies() {
        cookieDataStore.updateData { emptyMap() }
    }
}

class TokenRepository(
    private val dataStore: DataStore<TokenSave>
) {
    val refreshToken: Flow<String?> = dataStore.data.map { it.refreshToken }
    suspend fun setRefreshToken(value: String) {
        dataStore.updateData {
            it.copy(refreshToken = value)
        }
    }
}