package com.imcys.bilibilias.core.domain.model

data class DanmuRequest(
    val aid: Long,
    val cid: Long,
    val duration: Int,
    val title: String,
    val width: Int,
    val height: Int
)