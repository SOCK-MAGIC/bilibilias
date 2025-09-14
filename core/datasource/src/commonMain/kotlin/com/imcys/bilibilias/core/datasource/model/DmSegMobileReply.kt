package com.imcys.bilibilias.core.datasource.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class DmSegMobileReply(
    @ProtoNumber(1)
    val elems: List<DanmakuElem> = emptyList(),
    @ProtoNumber(3)
    val aiFlag: DanmakuAIFlag = DanmakuAIFlag(),
    @ProtoNumber(5)
    val colorfulSrc: List<DmColorful> = emptyList()
)

/**
 * @param id 弹幕dmid
 * @param progress 弹幕出现位置(单位ms)
 * @param mode 弹幕类型：
 *             - 1 2 3:普通弹幕
 *             - 4:底部弹幕
 *             - 5:顶部弹幕
 *             - 6:逆向弹幕
 *             - 7:高级弹幕
 *             - 8:代码弹幕
 *             - 9:BAS弹幕(pool必须为2)
 * @param fontsize 弹幕字号
 * @param color 弹幕颜色
 * @param midHash 送者mid hash
 * @param content 弹幕正文
 * @param ctime 发送时间
 * @param weight 权重 用于屏蔽等级 区间:[1,10]
 * @param action 动作
 * @param pool 弹幕池
 *             - 0:普通池
 *             - 1:字幕池
 *             - 2:特殊池(代码/BAS弹幕)
 * @param idStr 弹幕dmid str
 * @param attr 弹幕属性位(bin求AND)
 *             - bit0:保护
 *             - bit1:直播
 *             - bit2:高赞
 * @param animation
 * @param colorful 大会员专属颜色
 */
@Serializable
data class DanmakuElem(
    @ProtoNumber(1) val id: Long = 0,
    @ProtoNumber(2) val progress: Int = 0,
    @ProtoNumber(3) val mode: Int = 0,
    @ProtoNumber(4) val fontSize: Int = 0,
    @ProtoNumber(5) val color: Int = 0,
    @ProtoNumber(6) val midHash: String = "",
    @ProtoNumber(7) val content: String = "",
    @ProtoNumber(8) val ctime: Long = 0,
    @ProtoNumber(9) val weight: Int = 0,
    @ProtoNumber(10) val action: String = "",
    @ProtoNumber(11) val pool: Int = 0,
    @ProtoNumber(12) val idStr: String = "",
    @ProtoNumber(13) val attr: Int = 0,
    @ProtoNumber(22) val animation: String = "",
    @ProtoNumber(24) val colorful: DmColorfulType = DmColorfulType.NoneType,
)

/**
 * @param type 颜色类型
 */
@Serializable
data class DmColorful(val type: DmColorfulType = DmColorfulType.NoneType, val src: String = "")
enum class DmColorfulType {
    @ProtoNumber(1)
    NoneType,            // 无

    @ProtoNumber(60001)
    VipGradualColor     // 渐变色
}

// 弹幕ai云屏蔽列表
@Serializable
data class DanmakuAIFlag(val dmFlags: List<DanmakuFlag> = emptyList())

// 弹幕ai云屏蔽条目
@Serializable
data class DanmakuFlag(
    @ProtoNumber(1) val dmid: Long = 0,
    @ProtoNumber(2) val flag: Int = 0
)