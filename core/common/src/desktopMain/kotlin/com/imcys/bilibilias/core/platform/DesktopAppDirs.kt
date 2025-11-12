package com.imcys.bilibilias.core.platform


import com.imcys.bilibilias.core.io.SystemPath
import com.imcys.bilibilias.core.io.inSystem
import com.imcys.bilibilias.core.io.resolve
import kotlinx.io.files.Path

class DesktopAppDirs : AppDirs {
    private val filesDir = Path(getEnv("APPDATA"), "BilibiliAs")
    override val cacheDir: SystemPath = filesDir.resolve("cache").inSystem
    override val dataDir: SystemPath = filesDir.resolve("data").inSystem
    override val logsDir: SystemPath = filesDir.resolve("logs").inSystem
    override val defaultBaseMediaCacheDir: SystemPath = filesDir.resolve("media-downloads").inSystem

    private fun getEnv(key: String): String {
        return System.getenv(key)
            ?: throw IllegalStateException("Environment variable $key not found.")
    }
}