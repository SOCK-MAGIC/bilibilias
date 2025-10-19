package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.BuildConfig
import com.imcys.bilibilias.core.ass.AssWriter
import com.imcys.bilibilias.core.ass.Canvas
import com.imcys.bilibilias.core.ass.Color
import com.imcys.bilibilias.core.ass.DanmuType
import com.imcys.bilibilias.core.ass.DisplayConfiguration
import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.domain.model.DanmuRequest
import com.imcys.bilibilias.core.io.resolve
import com.imcys.bilibilias.core.logging.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid
import com.imcys.bilibilias.core.ass.DanmakuElem as AssElem
import com.imcys.bilibilias.core.datasource.model.DanmakuElem as BiliElem

class GetDmUseCase(private val api: BilibiliApi) {
    private val logger = logger<GetDmUseCase>()

    suspend operator fun invoke(request: DanmuRequest): String = withContext(Dispatchers.IO) {
        val dmSeg = api.dmSegMobile(request.aid, request.cid, request.duration).flatMap { it.elems }
        val danmus = dmSeg.map { it.converter() }

        logger.debug { "弹幕 ${dmSeg.size} ${dmSeg.firstOrNull()}" }

        val path = BuildConfig.MEDIA_DOWNLOAD.resolve(Uuid.random().toString())

        val writer = AssWriter(path)
        val config = config()

        val canvas = Canvas(config)

        writer.writerHeader(config)
        writer.use { writer ->
            danmus.mapNotNull {
                canvas.draw(it)
            }
                .forEach { writer.writer(it) }
        }

        path.toString()
    }

    fun config() = DisplayConfiguration()

    fun BiliElem.converter(): AssElem {
        return AssElem(
            progress = progress,
            mode = DanmuType.valueOf(mode),
            color = Color(color),
            content = content
        )
    }
}