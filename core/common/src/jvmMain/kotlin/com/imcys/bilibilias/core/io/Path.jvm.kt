package com.imcys.bilibilias.core.io

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import java.io.File

fun Path.toFile(): File = File(this.toString())
fun SystemPath.toFile(): File = path.toFile()
fun File.toKtPath(): Path = Path(this.toString())

/**
 * @see FileSystem.createDirectories
 */
fun SystemPath.createDirectories(mustCreate: Boolean = false): Unit =
    SystemFileSystem.createDirectories(path, mustCreate)
actual val SystemPath.absolutePath: String
    @Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
    get() = path.file.absolutePath