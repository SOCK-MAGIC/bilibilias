package com.imcys.bilibilias.core.testing.util

import com.imcys.bilibilias.core.data.util.ErrorMessage
import com.imcys.bilibilias.core.data.util.ErrorMonitor
import com.imcys.bilibilias.core.data.util.MessageType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class TestErrorMonitor(networkMonitor: TestNetworkMonitor) : ErrorMonitor {
    override var offlineMessage: String? = "offline"
    override val isOffline: Flow<Boolean> = networkMonitor.isOnline.map { !it }
    override fun addShortErrorMessage(
        error: String,
        type: MessageType,
        label: String?,
        action: (() -> Unit)?,
    ): String = "1"

    override fun addLongErrorMessage(
        error: String,
        type: MessageType,
        label: String?,
        action: (() -> Unit)?,
    ): String = "2"

    override fun addIndefiniteErrorMessage(
        error: String,
        type: MessageType,
        label: String?,
        action: (() -> Unit)?,
    ): String = "3"

    override fun clearErrorMessage(id: String) {
        // Do nothing
    }

    override val errorMessage: Flow<ErrorMessage?>
        get() = flowOf(ErrorMessage("Error Message", "1"))
}
