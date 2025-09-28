package com.imcys.bilibilias.core.ffmpeg

import kotlinx.coroutines.flow.StateFlow

interface MediaProcessor {

    val isRunning: StateFlow<Boolean>
    val progress: StateFlow<Int>
    suspend fun process(request: ProcessRequest): ProcessResult
}