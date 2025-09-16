package com.imcys.bilibilias.core.datastore.model

data class Quality(
    val id: Int,
    val description: String,
)

object AudioQualities {
    val ULTRA_HIGH_100010 = Quality(100010, "100010")
    val ULTRA_HIGH_100009 = Quality(100009, "100009")
    val ULTRA_HIGH_100008 = Quality(100008, "100008")
    val HI_RES_LOSSLESS = Quality(30251, "Hi-Res无损")
    val DOLBY_ATMOS = Quality(30250, "杜比全景声")
    val BITRATE_192K = Quality(30280, "192K")
    val BITRATE_132K = Quality(30232, "132K")
    val BITRATE_64K = Quality(30216, "64K")
    val all = listOf(
        ULTRA_HIGH_100010,
        ULTRA_HIGH_100009,
        ULTRA_HIGH_100008,
        HI_RES_LOSSLESS,
        DOLBY_ATMOS,
        BITRATE_192K,
        BITRATE_132K,
        BITRATE_64K,
    )
}

object VideoQualities {
    val R4320P = Quality(127, "8K")
    val DolbyV = Quality(126, "杜比视界")
    val HDR = Quality(125, "HDR 真彩色")
    val R2160P = Quality(120, "4K")
    val R1080P60 = Quality(116, "1080P60 高帧率")
    val R1080PPlus = Quality(112, "1080P 高码率")
    val AI = Quality(100, "AI修复")
    val R1080P = Quality(80, "1080P 高清")
    val R720P60 = Quality(74, "720P60 高帧率")
    val R720P = Quality(64, "720P 准高清")
    val R480P = Quality(32, "480P 标清")
    val R360P = Quality(16, "360P 流畅")
    val all = listOf(
        R4320P,
        DolbyV,
        HDR,
        R2160P,
        R1080P60,
        R1080PPlus,
        AI,
        R1080P,
        R720P60,
        R720P,
        R480P,
        R360P,
    )
}