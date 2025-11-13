package com.imcys.bilibilias.core.datasource.local

import androidx.datastore.core.DataStore
import com.imcys.bilibilias.core.model.Credentials
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CredentialsDataSource(
    private val credentialsDataStore: DataStore<Credentials>,
) {

    val credentialsFlow: Flow<Credentials> = credentialsDataStore.data
    suspend fun getCredentials(): Credentials = credentialsDataStore.data.first()

    // --- 更新方法 ---
    suspend fun saveCredentials(newCredentials: Credentials) {
        credentialsDataStore.updateData {
            // 直接返回新的对象以完全覆盖旧的
            newCredentials
        }
    }

    suspend fun updateTokens(accessToken: String, expiresAt: Long, refreshToken: String? = null) {
        credentialsDataStore.updateData { currentCredentials ->
            currentCredentials.copy(
                accessToken = accessToken,
                expiresAt = expiresAt,
                // 如果传入的 refreshToken 不为 null，则使用它；否则，保留旧的 refreshToken
                refreshToken = refreshToken ?: currentCredentials.refreshToken
            )
        }
    }

    suspend fun updateCookies(newCookies: Map<String, String>) {
        credentialsDataStore.updateData { currentCredentials ->
            currentCredentials.copy(cookie = newCookies)
        }
    }

    suspend fun updateUserId(mid: Long) {
        credentialsDataStore.updateData { currentCredentials ->
            currentCredentials.copy(mid = mid)
        }
    }

    suspend fun clearCredentials() {
        credentialsDataStore.updateData {
            Credentials()
        }
    }
}