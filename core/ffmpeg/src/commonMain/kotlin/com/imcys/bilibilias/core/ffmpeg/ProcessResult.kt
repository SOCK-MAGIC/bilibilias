package com.imcys.bilibilias.core.ffmpeg

sealed interface ProcessResult {
    object Success : ProcessResult
    data class Failure(val errorMessage: String) : ProcessResult
    object Cancelled : ProcessResult
}