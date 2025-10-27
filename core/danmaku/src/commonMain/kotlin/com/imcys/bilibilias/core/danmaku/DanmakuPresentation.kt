package com.imcys.bilibilias.core.danmaku

class DanmakuPresentation(
    val danmaku: DanmakuInfo,
    val isSelf: Boolean,
) {
    val id get() = danmaku.id
}