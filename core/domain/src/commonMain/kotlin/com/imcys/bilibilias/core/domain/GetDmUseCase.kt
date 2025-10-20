package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.BuildConfig
import com.imcys.bilibilias.core.ass.AssWriter
import com.imcys.bilibilias.core.ass.Danmaku
import com.imcys.bilibilias.core.ass.DanmakuLoader
import com.imcys.bilibilias.core.ass.RenderOptions
import com.imcys.bilibilias.core.ass.Studio
import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.datasource.model.DanmakuElem
import com.imcys.bilibilias.core.domain.model.DanmuRequest
import com.imcys.bilibilias.core.io.resolve
import com.imcys.bilibilias.core.logging.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid

class GetDmUseCase(private val api: BilibiliApi) {
    private val logger = logger<GetDmUseCase>()

    suspend operator fun invoke(request: DanmuRequest): String = withContext(Dispatchers.IO) {
        request.width
        request.height
        val dmSeg = api.dmSegMobile(request.aid, request.cid, request.duration).flatMap { it.elems }

        logger.debug { "弹幕 ${dmSeg.size} ${dmSeg.firstOrNull()}" }

        val path = BuildConfig.MEDIA_DOWNLOAD.resolve(Uuid.random().toString())

        val loader = BilibiliDanmakuLoader(dmSeg)

        val studio = Studio(RenderOptions.Default, loader.load())
        val subtitles = studio.generate()

        val writer = AssWriter(path)
        writer.writerHeader(RenderOptions.Default)
        writer.writerBody(subtitles)
        writer.close()

        path.toString()
    }
}

private class BilibiliDanmakuLoader(private val dmSeg: List<DanmakuElem>) : DanmakuLoader {
    override fun load(): List<Danmaku> {
        return dmSeg.mapNotNull {
            Danmaku.fromRawData(
                it.progress / 1000,
                it.mode,
                it.color,
                it.content,
                it.fontSize
            )
        }
    }
}