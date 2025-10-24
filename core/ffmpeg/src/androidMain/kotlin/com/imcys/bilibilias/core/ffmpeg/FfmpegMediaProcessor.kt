package com.imcys.bilibilias.core.ffmpeg

import android.content.Context
import com.antonkarpenko.ffmpegkit.FFmpegKit
import com.antonkarpenko.ffmpegkit.FFmpegKitConfig
import com.eygraber.uri.toAndroidUri
import com.imcys.bilibilias.core.logging.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import android.net.Uri as AndroidUri

internal class FfmpegMediaProcessor(
    private val context: Context,
) : MediaProcessor {

    private val _isRunning = MutableStateFlow(false)
    override val isRunning = _isRunning.asStateFlow()

    private val _progress = MutableStateFlow(0)
    override val progress = _progress.asStateFlow()

    private val logger = logger<MediaProcessor>()

    // 原子布尔值确保并发安全
    @OptIn(ExperimentalAtomicApi::class)
    private val operationInProgress = AtomicBoolean(false)

    @OptIn(ExperimentalAtomicApi::class)
    override suspend fun process(request: ProcessRequest): ProcessResult {
        logger.debug { "muxMedia called with request: $request" }
        // 检查是否已有操作在运行，防止并发执行
        if (!operationInProgress.compareAndSet(expectedValue = false, newValue = true)) {
            logger.warn { "muxMedia called while another operation was in progress." }
            return ProcessResult.Failure("Another operation is already in progress.")
        }

        // 重置状态
        _isRunning.value = true
        _progress.value = 0

        // 使用 withContext 确保 FFmpeg 命令构建等 CPU 密集型任务在后台线程执行
        return withContext(Dispatchers.IO) {
            try {
                // 使用 suspendCancellableCoroutine 桥接回调
                suspendCancellableCoroutine { continuation ->
                    val command = buildCommand(request)
                    logger.debug { "Executing FFmpeg command: $command" }

                    // FFmpegKit 提供了获取总时长的功能，用于计算进度
                    var durationInSeconds = 0.0

                    val session = FFmpegKit.executeAsync(
                        command,
                        { session ->
                            val returnCode = session.returnCode
                            val result = when {
                                returnCode.isValueSuccess -> ProcessResult.Success
                                returnCode.isValueError -> ProcessResult.Failure("FFmpeg command failed. Log: ${session.output}")
                                else -> ProcessResult.Cancelled
                            }
                            logger.info { "FFmpeg process finished with result: $result" }
                            if (continuation.isActive) {
                                continuation.resume(result) { _, _, _ ->

                                }
                            }
                        },
                        { log -> logger.debug { "FFmpeg Log: ${log.message}" } },
                        { statistics ->
                            if (durationInSeconds > 0) {
                                val timeInSeconds = statistics.time / 1000.0
                                val newProgress =
                                    ((timeInSeconds / durationInSeconds) * 100).toInt()
                                _progress.value = newProgress.coerceIn(0, 100)
                            }
                        }
                    )

                    // 处理协程取消
                    continuation.invokeOnCancellation {
                        logger.warn { "Coroutine cancelled. Cancelling FFmpeg session ${session.sessionId}" }
                        FFmpegKit.cancel(session.sessionId)
                    }
                }
            } finally {
                // 确保在操作结束或异常时重置状态
                _isRunning.value = false
                operationInProgress.store(false)
                _progress.value = 100
            }
        }
    }

    private fun buildCommand(request: ProcessRequest): String {
        // 1. 处理主要输入
        val mainInputs = request.inputUris.joinToString(separator = " ") { uri ->
            val file = AndroidUri.fromFile(File(uri.toString()))
            "-i ${FFmpegKitConfig.getSafParameterForRead(context, file)}"
        }

        // 2. 处理字幕输入
        val subtitleInputs = request.subtitleTracks.joinToString(separator = " ") { track ->
            val file = AndroidUri.fromFile(File(track.uri.toString()))
            "-i ${FFmpegKitConfig.getSafParameterForRead(context, file)}"
        }

        val allInputs = "$mainInputs $subtitleInputs".trim()
        val output =
            FFmpegKitConfig.getSafParameterForWrite(context, request.outputUri.toAndroidUri())

        // 3. 根据字幕模式构建命令的核心部分
        return when (request.subtitleMode) {
            SubtitleMode.HARD_SUB -> {
                // 硬字幕需要视频滤镜，并且强制重新编码视频
                // 注意：这里假设第一个字幕轨道用于烧录。硬字幕通常只支持一个。
                val subtitleFile = request.subtitleTracks.firstOrNull()?.let {
                    val file = AndroidUri.fromFile(File(it.uri.toString()))
                    FFmpegKitConfig.getSafParameterForRead(context, file)
                } ?: ""
                // -c:a copy 复制音频流, -c:v libx264 重新编码视频流
                "$mainInputs -vf \"subtitles='$subtitleFile'\" -c:a copy -c:v libx264 $output"
            }

            SubtitleMode.SOFT_SUB -> {
                // 软字幕需要映射所有流并添加元数据
                val streamMappings = buildStreamMappings(request)
                val metadata = buildSubtitleMetadata(request)
                // -c:s mov_text 确保字幕与MP4兼容
                "$allInputs $streamMappings -c:v copy -c:a copy -c:s mov_text $metadata $output"
            }
        }
    }

    private fun buildStreamMappings(request: ProcessRequest): String {
        val inputCount = request.inputUris.size
        val subtitleCount = request.subtitleTracks.size
        // 映射所有流: 视频(v), 音频(a), 字幕(s)
        // e.g., -map 0:v -map 1:a -map 2:s
        val mappings = StringBuilder()
        // 映射主输入的视频和音频
        mappings.append("-map 0:v? -map 1:a? ")
        // 映射所有字幕流
        for (i in 0 until subtitleCount) {
            mappings.append("-map ${inputCount + i}:s? ")
        }
        return mappings.toString().trim()
    }

    private fun buildSubtitleMetadata(request: ProcessRequest): String {
        val metadata = StringBuilder()
        request.subtitleTracks.forEachIndexed { index, track ->
            // 为每个字幕流设置语言元数据
            metadata.append("-metadata:s:s:$index language=${track.language} ")
            // 如果是默认字幕，设置 disposition
            if (track.isDefault) {
                metadata.append("-disposition:s:$index default ")
            }
        }
        return metadata.toString().trim()
    }
}