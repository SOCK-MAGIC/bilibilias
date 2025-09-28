package com.imcys.bilibilias

import com.imcys.bilibilias.mp.BuildKonfig
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.toKotlinxIoPath
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

object BuildConfig {
    val APPLICATION_ID: String = BuildKonfig.packageName

    val DEBUG: Boolean = BuildKonfig.debugBuild

    val LOG_DIR: Path = PlatformFile(FileKit.filesDir, "logs").toKotlinxIoPath()
    val DATASTORE_DIR: Path = PlatformFile(FileKit.filesDir, "datastore").toKotlinxIoPath()
    val MEDIA_DOWNLOAD: Path = PlatformFile(FileKit.filesDir, "media-download").toKotlinxIoPath()

    /**
     * 创建文件夹
     */
    fun createDirectory(path: Path) {
        if (!SystemFileSystem.exists(path)) {
            SystemFileSystem.createDirectories(path)
        }
    }

    /**
     * 初始化文件夹
     */
    fun initDirectory() {
        createDirectory(LOG_DIR)
        createDirectory(DATASTORE_DIR)
        createDirectory(MEDIA_DOWNLOAD)
    }
}