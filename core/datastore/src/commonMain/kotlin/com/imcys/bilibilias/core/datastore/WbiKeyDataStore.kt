package com.imcys.bilibilias.core.datastore

import androidx.datastore.core.DataStore
import com.imcys.bilibilias.core.datastore.model.WbiKeysData
import kotlin.time.Clock

class WbiKeyDataStore(
    private val dataStore: DataStore<WbiKeysData>
) {
    val wbiKeysData = dataStore.data
    suspend fun updateKeys(imgKey: String, subKey: String) {
        dataStore.updateData {
            it.copy(imgKey = imgKey, subKey = subKey, fetchedAt = Clock.System.now())
        }
    }
}