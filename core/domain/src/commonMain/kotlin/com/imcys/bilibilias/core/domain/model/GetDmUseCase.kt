package com.imcys.bilibilias.core.domain.model

import com.imcys.bilibilias.BuildConfig
import com.imcys.bilibilias.core.ass.Danmu
import com.imcys.bilibilias.core.ass.DanmuType
import com.imcys.bilibilias.core.ass.Rgb
import com.imcys.bilibilias.core.ass.canvas.CanvasConfig
import com.imcys.bilibilias.core.ass.convert
import com.imcys.bilibilias.core.datasource.api.BilibiliApi
import com.imcys.bilibilias.core.datasource.model.DanmakuElem
import com.imcys.bilibilias.core.io.resolve

class GetDmUseCase(private val api: BilibiliApi) {
    suspend operator fun invoke(aid: Long, cid: Long) {
        val dmSeg = api.dmSegMobile(aid, cid, 0)
        val danmus = dmSeg.elems.map { it.toDanmu() }

        convert(
            dataProvider = danmus,
            title = "title",
            output = BuildConfig.LOG_DIR.resolve("test.dm"),
            canvasConfig = canvasConfig(),
            denylist = null
        )
    }


    fun canvasConfig(): CanvasConfig {
        return CanvasConfig.create(
            duration = 0.0,
            width = 1920,
            height = 1080,
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
            alpha = (1.0 - 0.7) * 255.0
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