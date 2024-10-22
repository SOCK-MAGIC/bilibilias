package com.imcys.bilibilias.home.ui.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserNavDataModel(
    @SerialName("code")
    val code: Int = 0,
    @SerialName("data")
    val `data`: Data = Data(),
    @SerialName("message")
    val message: String = "",
    @SerialName("ttl")
    val ttl: Int = 0,
) {
    @Serializable
    data class Data(
        @SerialName("allowance_count")
        val allowanceCount: Int = 0,
        @SerialName("answer_status")
        val answerStatus: Int = 0,
        @SerialName("email_verified")
        val emailVerified: Int = 0,
        @SerialName("face")
        val face: String = "",
        @SerialName("face_nft")
        val faceNft: Int = 0,
        @SerialName("face_nft_type")
        val faceNftType: Int = 0,
        @SerialName("has_shop")
        val hasShop: Boolean = false,
        @SerialName("is_jury")
        val isJury: Boolean = false,
        @SerialName("isLogin")
        val isLogin: Boolean = false,
        @SerialName("is_senior_member")
        val isSeniorMember: Int = 0,
        @SerialName("level_info")
        val levelInfo: LevelInfo = LevelInfo(),
        @SerialName("mid")
        val mid: Long = 0,
        @SerialName("mobile_verified")
        val mobileVerified: Int = 0,
        @SerialName("money")
        val money: Double = 0.0,
        @SerialName("moral")
        val moral: Int = 0,
        @SerialName("official")
        val official: Official = Official(),
        @SerialName("officialVerify")
        val officialVerify: OfficialVerify = OfficialVerify(),
        @SerialName("pendant")
        val pendant: Pendant = Pendant(),
        @SerialName("scores")
        val scores: Int = 0,
        @SerialName("shop_url")
        val shopUrl: String = "",
        @SerialName("uname")
        val uname: String = "",
        @SerialName("vip")
        val vip: Vip = Vip(),
        @SerialName("vip_avatar_subscript")
        val vipAvatarSubscript: Int = 0,
        @SerialName("vipDueDate")
        val vipDueDate: Long = 0,
        @SerialName("vip_nickname_color")
        val vipNicknameColor: String = "",
        @SerialName("vip_pay_type")
        val vipPayType: Int = 0,
        @SerialName("vipStatus")
        val vipStatus: Int = 0,
        @SerialName("vip_theme_type")
        val vipThemeType: Int = 0,
        @SerialName("vipType")
        val vipType: Int = 0,
        @SerialName("wbi_img")
        val wbiImg: WbiImg = WbiImg(),
    ) {
        @Serializable
        data class LevelInfo(
            @SerialName("current_exp")
            val currentExp: Int = 0,
            @SerialName("current_level")
            val currentLevel: Int = 0,
            @SerialName("current_min")
            val currentMin: Int = 0,
            @SerialName("next_exp")
            val nextExp: String = "",
        )

        @Serializable
        data class Official(
            @SerialName("desc")
            val desc: String = "",
            @SerialName("role")
            val role: Int = 0,
            @SerialName("title")
            val title: String = "",
            @SerialName("type")
            val type: Int = 0,
        )

        @Serializable
        data class OfficialVerify(
            @SerialName("desc")
            val desc: String = "",
            @SerialName("type")
            val type: Int = 0,
        )

        @Serializable
        data class Pendant(
            @SerialName("expire")
            val expire: Int = 0,
            @SerialName("image")
            val image: String = "",
            @SerialName("image_enhance")
            val imageEnhance: String = "",
            @SerialName("image_enhance_frame")
            val imageEnhanceFrame: String = "",
            @SerialName("name")
            val name: String = "",
            @SerialName("pid")
            val pid: Int = 0,
        )

        @Serializable
        data class Vip(
            @SerialName("avatar_subscript")
            val avatarSubscript: Int = 0,
            @SerialName("avatar_subscript_url")
            val avatarSubscriptUrl: String = "",
            @SerialName("due_date")
            val dueDate: Long = 0,
            @SerialName("nickname_color")
            val nicknameColor: String = "",
            @SerialName("role")
            val role: Int = 0,
            @SerialName("status")
            val status: Int = 0,
            @SerialName("theme_type")
            val themeType: Int = 0,
            @SerialName("tv_vip_pay_type")
            val tvVipPayType: Int = 0,
            @SerialName("tv_vip_status")
            val tvVipStatus: Int = 0,
            @SerialName("type")
            val type: Int = 0,
            @SerialName("vip_pay_type")
            val vipPayType: Int = 0,
        )

        @Serializable
        data class WbiImg(
            @SerialName("img_url")
            val imgUrl: String = "",
            @SerialName("sub_url")
            val subUrl: String = "",
        )
    }
}
