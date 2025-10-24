package com.imcys.bilibilias.core.ass

object DanmakufactoryLib {
    init {
        System.loadLibrary("danmakufactory")
    }

    external fun convertDanmakuFile(inputPath: String, outputPath: String): String
}