package com.imcys.bilibilias.core.ass

object Danmakufactory {
    init {
        System.loadLibrary("danmakufactory")
    }

    external fun convertDanmakuFile(inputPath: String, outputPath: String): String
}