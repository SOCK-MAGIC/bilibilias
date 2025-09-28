package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.BuildConfig
import com.imcys.bilibilias.core.ass.Danmu
import com.imcys.bilibilias.core.ass.DanmuType
import com.imcys.bilibilias.core.ass.Rgb
import com.imcys.bilibilias.core.ass.canvas.CanvasConfig
import com.imcys.bilibilias.core.ass.convert
import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.datasource.model.DanmakuElem
import com.imcys.bilibilias.core.domain.model.DanmuRequest
import com.imcys.bilibilias.core.io.resolve
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import kotlin.uuid.Uuid

class GetDmUseCase(private val api: BilibiliApi) {
    suspend operator fun invoke(request: DanmuRequest): String = withContext(Dispatchers.IO) {
        val dmSeg = api.dmSegMobile(request.aid, request.cid, request.duration).flatMap { it.elems }
        val danmus = dmSeg.map { it.toDanmu() }

        val path = BuildConfig.MEDIA_DOWNLOAD.resolve(Uuid.random().toString())
        convert(
            dataProvider = danmus,
            title = request.title,
            output = path,
            canvasConfig = canvasConfig(request.width, request.height),
            denylist = null
        )
        path.toString()
    }


    fun canvasConfig(width: Int, height: Int): CanvasConfig {
        return CanvasConfig(
            duration = 15.0,
            width = width,
            height = height,
            font = "黑体",
            fontSize = 25,
            widthRatio = 1.2,
            horizontalGap = 20.0,
            laneSize = 32,
            floatPercentage = 0.5,
            bold = false,
            outline = 0.8,
            timeOffset = 0.0,
            bottomPercentage = 0.3,
            alpha = (0.3 * 255.0).roundToInt()
        )
    }

    fun DanmakuElem.toDanmu(): Danmu {
        return Danmu(
            timelineS = progress / 1000.0,
            content = content,
            fontsize = fontSize,
            rgb = Rgb(
                r = (color shr 16) and 0xFF,
                g = (color shr 8) and 0xFF,
                b = color and 0xFF
            ),
            type = DanmuType.valueOf(mode)
        )
    }
}