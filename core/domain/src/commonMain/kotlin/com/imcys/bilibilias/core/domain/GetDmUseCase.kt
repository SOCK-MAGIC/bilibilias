package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.BuildConfig
import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.datasource.model.DanmakuElem
import com.imcys.bilibilias.core.domain.model.DanmuRequest
import com.imcys.bilibilias.core.io.resolve
import com.imcys.bilibilias.core.logging.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString
import kotlin.uuid.Uuid

class GetDmUseCase(private val api: BilibiliApi) {
    private val logger = logger<GetDmUseCase>()

    suspend operator fun invoke(request: DanmuRequest): String = withContext(Dispatchers.IO) {
        val dmSeg = api.dmSegMobile(request.aid, request.cid, request.duration).flatMap { it.elems }

//        val tempPath = BuildConfig.MEDIA_DOWNLOAD.resolve("danmaku.xml")
        val danmakuOutputPath =
            BuildConfig.MEDIA_DOWNLOAD.resolve(Uuid.random().toString())

//        val xmlWriter = XmlWriter(tempPath)
//        xmlWriter.use {
//            xmlWriter.writer(dmSeg, request.cid)
//        }
//        DanmakufactoryLib.convertDanmakuFile(tempPath.toString(), danmakuOutputPath.toString())

//        SystemFileSystem.sink( danmakuOutputPath).asByteWriteChannel().writeByteArray(dmSeg)

        danmakuOutputPath.toString()
    }
}

class XmlWriter(
    path: Path
) : AutoCloseable {
    private val sink = SystemFileSystem.sink(path).buffered()
    fun writer(elem: List<DanmakuElem>, cid: Long) {
        val head = """
<?xml version="1.0" encoding="UTF-8"?><i><chatserver></chatserver><chatid>$cid</chatid><mission></mission><maxlimit></maxlimit><state></state><real_name></real_name><source></source>
        """.trimIndent()
        sink.writeString(head)
        elem.forEach {
            buildBody(it)
        }
        sink.writeString("</i>")
    }

    private fun buildBody(elem: DanmakuElem) {
        val body = buildString(256) {
            val text = with(elem) {
                "<d p=\"${progress / 1000.0},$mode,$fontSize,$color,$ctime,$pool,$midHash,$idStr\">${content.escapeXml()}</d>"
            }
            append(text)
        }
        sink.writeString(body)
    }

    private fun String.escapeXml(): String {
        return replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    override fun close() {
        sink.close()
    }
}