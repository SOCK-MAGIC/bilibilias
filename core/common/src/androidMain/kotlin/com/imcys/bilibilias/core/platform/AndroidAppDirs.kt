package com.imcys.bilibilias.core.platform

import android.content.Context
import android.os.Environment
import com.imcys.bilibilias.core.io.SystemPath
import com.imcys.bilibilias.core.io.inSystem
import com.imcys.bilibilias.core.io.resolve
import com.imcys.bilibilias.core.io.toKtPath
import java.io.File

class AndroidAppDirs(
    context: Context,
) : AppDirs {
    override val cacheDir: SystemPath =
        (context.cacheDir ?: File("")).toKtPath().inSystem
    override val dataStoreDir: SystemPath = File(context.filesDir, "datastore").toKtPath().inSystem
    override val dataDir: SystemPath =
        (context.filesDir ?: File("")).toKtPath().inSystem
    override val logsDir: SystemPath =
        File(context.filesDir, "logs").toKtPath().inSystem
    private val fallbackInternalBaseMediaCacheDir = dataDir.resolve("media-downloads")
    override val defaultBaseMediaCacheDir: SystemPath =
        context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.toKtPath()?.inSystem
            ?: fallbackInternalBaseMediaCacheDir
}