package com.imcys.bilibilias.core.ffmpeg

import com.eygraber.uri.Uri

/**
 * 封装一个媒体合并操作所需的所有参数。
 * @param inputUris 主要的媒体输入文件 (视频和音频)。
 * @param outputUri 输出文件的 Uri。
 * @param subtitleTracks 一个字幕轨道的列表，可以为空。
 * @param subtitleMode 定义字幕的处理方式 (软字幕或硬字幕)。
 */
data class ProcessRequest(
    val inputUris: List<Uri>,
    val outputUri: Uri,
    val subtitleTracks: List<SubtitleTrack> = emptyList(),
    val subtitleMode: SubtitleMode = SubtitleMode.SOFT_SUB // 默认使用软字幕
)

/**
 * 定义字幕的嵌入方式。
 */
enum class SubtitleMode {
    /**
     * 软字幕 (Soft subs): 作为一条独立的、可选择的轨道嵌入到容器中 (例如 MP4, MKV)。
     * 这是推荐的方式，因为它不影响视频质量且允许用户开关字幕。
     * 使用 -c:s mov_text (for MP4) or -c:s copy (for MKV)
     */
    SOFT_SUB,

    /**
     * 硬字幕 (Hard subs): 将字幕直接“烧录”或“渲染”到视频帧上。
     * 这会强制重新编码视频，速度较慢，且可能导致质量损失。
     * 字幕将成为视频的一部分，无法被关闭。
     * 使用 -vf subtitles filter.
     */
    HARD_SUB
}

/**
 * 描述一个字幕轨道的信息。
 * @param uri 字幕文件的 Uri (例如 .srt, .ass 文件)。
 * @param language 字幕的语言代码 (遵循 ISO 639-2/B 标准, e.g., "eng", "spa", "jpn")。
 * @param isDefault 此轨道是否应被播放器默认为开启状态。
 */
data class SubtitleTrack(
    val uri: Uri,
    val language: String,
    val isDefault: Boolean = false
)