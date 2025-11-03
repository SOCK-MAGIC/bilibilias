package com.imcys.bilibilias.danmaku.api

object DanmakuSanitizer {
    fun sanitize(danmaku: DanmakuInfo): DanmakuInfo = danmaku.run {
        if (text.indexOf("\n") == -1) return@run this

        copy(
            content = content.copy(
                text = text
                    .replace("\n\r", " ")
                    .replace("\r\n", " ")
                    .replace("\n", " ")
                    .trim(),
            ),
        )
    }
}