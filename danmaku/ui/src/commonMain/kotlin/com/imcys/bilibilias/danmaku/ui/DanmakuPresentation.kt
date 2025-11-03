package com.imcys.bilibilias.danmaku.ui

class DanmakuPresentation(
    val danmaku: DanmakuInfo,
    val isSelf: Boolean,
) {
    val id get() = danmaku.id
}