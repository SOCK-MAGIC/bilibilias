package com.imcys.bilibilias.core.platform

import com.imcys.bilibilias.core.io.SystemPath
import kotlinx.io.files.SystemFileSystem

interface AppDirs {
    val cacheDir: SystemPath
    val dataDir: SystemPath
    val logsDir: SystemPath
    val dataStoreDir: SystemPath
    /**
     * Base directory of media cache downloads.
     *
     * * Android: external private storage or internal private if external is unavailable.
     * * Desktop: [dataDir]`/media-downloads` by default, can be changed by settings.
     */
    val defaultBaseMediaCacheDir: SystemPath
}

fun AppDirs.createDirectories() {
    listOf(
        cacheDir,
        dataDir,
        logsDir,
        defaultBaseMediaCacheDir
    ).forEach { systemPath ->
        SystemFileSystem.createDirectories(systemPath.path)
    }
}