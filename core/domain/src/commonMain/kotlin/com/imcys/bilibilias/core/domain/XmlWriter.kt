package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.datasource.model.DanmakuElem
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString

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