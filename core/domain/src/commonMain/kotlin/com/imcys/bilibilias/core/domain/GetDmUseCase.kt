package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.BuildConfig
import com.imcys.bilibilias.core.ass.Danmakufactory
import com.imcys.bilibilias.core.datasource.api.BilibiliApi
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

        logger.debug { "弹幕总数 ${dmSeg.size} ${dmSeg.firstOrNull()}" }

        val tempPath = BuildConfig.MEDIA_DOWNLOAD.resolve("danmaku.xml")
        val danmakuOutputPath =
            BuildConfig.MEDIA_DOWNLOAD.resolve(Uuid.random().toString() + ".ass")

        val xmlWriter = XmlWriter(tempPath)
        xmlWriter.use {
            xmlWriter.writer(dmSeg, request.cid)
        }
        Danmakufactory.convertDanmakuFile(tempPath.toString(), danmakuOutputPath.toString())

        danmakuOutputPath.toString()
    }
}