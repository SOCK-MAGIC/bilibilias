package com.imcys.bilibilias.core.datastore

import com.imcys.bilibilias.core.io.SystemPath
import com.imcys.bilibilias.core.io.resolve

actual fun resolveDataStoreFile(dir: SystemPath, filename: String): SystemPath {
    return dir.resolve(filename)
}