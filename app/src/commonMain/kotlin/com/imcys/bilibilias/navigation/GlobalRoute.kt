package com.imcys.bilibilias.navigation

import com.imcys.bilibilias.core.navigation.AsNavKey
import kotlinx.serialization.Serializable

@Serializable
data object SearchRoute : AsNavKey {
    override val isTopLevel: Boolean
        get() = true
}

@Serializable
data object CacheRoute : AsNavKey {
    override val isTopLevel: Boolean
        get() = true
}

@Serializable
data object LoginRoute : AsNavKey {
    override val isTopLevel: Boolean
        get() = false
}

@Serializable
data object SettingRoute : AsNavKey {
    override val isTopLevel: Boolean
        get() = false
}

@Serializable
data class PlayerRoute(
    val aid: Long,
    val bvid: String,
    val cid: Long,
) : AsNavKey {
    override val isTopLevel: Boolean
        get() = false
}