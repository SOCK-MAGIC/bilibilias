package com.imcys.bilibilias.core.datasource.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerInfo(
    @SerialName("aid")
    val aid: Long,
    @SerialName("allow_bp")
    val allowBp: Boolean,
    @SerialName("answer_status")
    val answerStatus: Int,
    @SerialName("block_time")
    val blockTime: Int,
    @SerialName("bvid")
    val bvid: String,
    @SerialName("cid")
    val cid: Long,
    @SerialName("disable_show_up_info")
    val disableShowUpInfo: Boolean,
    @SerialName("elec_high_level")
    val elecHighLevel: ElecHighLevel,
    @SerialName("fawkes")
    val fawkes: Fawkes,
    @SerialName("has_next")
    val hasNext: Boolean,
    @SerialName("interaction")
    val interaction: Interaction,
    @SerialName("ip_info")
    val ipInfo: IpInfo,
    @SerialName("is_owner")
    val isOwner: Boolean,
    @SerialName("is_ugc_pay_preview")
    val isUgcPayPreview: Boolean,
    @SerialName("is_upower_exclusive")
    val isUpowerExclusive: Boolean,
    @SerialName("is_upower_exclusive_with_qa")
    val isUpowerExclusiveWithQa: Boolean,
    @SerialName("is_upower_play")
    val isUpowerPlay: Boolean,
    @SerialName("last_play_cid")
    val lastPlayCid: Long,
    @SerialName("last_play_time")
    val lastPlayTime: Int,
    @SerialName("level_info")
    val levelInfo: LevelInfo,
    @SerialName("login_mid")
    val loginMid: Int,
    @SerialName("login_mid_hash")
    val loginMidHash: String,
    @SerialName("max_limit")
    val maxLimit: Int,
    @SerialName("name")
    val name: String,
    @SerialName("need_login_subtitle")
    val needLoginSubtitle: Boolean,
    @SerialName("no_share")
    val noShare: Boolean,
    @SerialName("now_time")
    val nowTime: Int,
    @SerialName("online_count")
    val onlineCount: Int,
    @SerialName("online_switch")
    val onlineSwitch: OnlineSwitch,
    @SerialName("options")
    val options: Options,
    @SerialName("page_no")
    val pageNo: Int,
    @SerialName("permission")
    val permission: String,
    @SerialName("player_icon")
    val playerIcon: PlayerIcon,
    @SerialName("preview_toast")
    val previewToast: String,
    @SerialName("role")
    val role: String,
    @SerialName("show_switch")
    val showSwitch: ShowSwitch,
    @SerialName("subtitle")
    val subtitle: Subtitle,
    @SerialName("toast_block")
    val toastBlock: Boolean,
    @SerialName("vip")
    val vip: Vip
) {
    @Serializable
    data class ElecHighLevel(
        @SerialName("button_text")
        val buttonText: String,
        @SerialName("intro")
        val intro: String,
        @SerialName("jump_url")
        val jumpUrl: String,
        @SerialName("new")
        val new: Boolean,
        @SerialName("privilege_type")
        val privilegeType: Int,
        @SerialName("qa_title")
        val qaTitle: String,
        @SerialName("question_text")
        val questionText: String,
        @SerialName("show_button")
        val showButton: Boolean,
        @SerialName("sub_title")
        val subTitle: String,
        @SerialName("title")
        val title: String
    )

    @Serializable
    data class Fawkes(
        @SerialName("config_version")
        val configVersion: Int,
        @SerialName("ff_version")
        val ffVersion: Int
    )

    @Serializable
    data class Interaction(
        @SerialName("error_toast")
        val errorToast: String,
        @SerialName("graph_version")
        val graphVersion: Int,
        @SerialName("history_node")
        val historyNode: HistoryNode,
        @SerialName("mark")
        val mark: Int,
        @SerialName("msg")
        val msg: String,
        @SerialName("need_reload")
        val needReload: Int
    ) {
        @Serializable
        data class HistoryNode(
            @SerialName("cid")
            val cid: Long,
            @SerialName("node_id")
            val nodeId: Int,
            @SerialName("title")
            val title: String
        )
    }

    @Serializable
    data class IpInfo(
        @SerialName("city")
        val city: String,
        @SerialName("country")
        val country: String,
        @SerialName("ip")
        val ip: String,
        @SerialName("province")
        val province: String,
        @SerialName("zone_id")
        val zoneId: Int,
        @SerialName("zone_ip")
        val zoneIp: String
    )

    @Serializable
    data class LevelInfo(
        @SerialName("current_exp")
        val currentExp: Int,
        @SerialName("current_level")
        val currentLevel: Int,
        @SerialName("current_min")
        val currentMin: Int,
        @SerialName("level_up")
        val levelUp: Long,
        @SerialName("next_exp")
        val nextExp: Int
    )

    @Serializable
    data class OnlineSwitch(
        @SerialName("enable_gray_dash_playback")
        val enableGrayDashPlayback: String,
        @SerialName("new_broadcast")
        val newBroadcast: String,
        @SerialName("realtime_dm")
        val realtimeDm: String,
        @SerialName("subtitle_submit_switch")
        val subtitleSubmitSwitch: String
    )

    @Serializable
    data class Options(
        @SerialName("is_360")
        val is360: Boolean,
        @SerialName("without_vip")
        val withoutVip: Boolean
    )

    @Serializable
    data class PlayerIcon(
        @SerialName("ctime")
        val ctime: Int
    )

    @Serializable
    data class ShowSwitch(
        @SerialName("long_progress")
        val longProgress: Boolean
    )

    @Serializable
    data class Subtitle(
        @SerialName("allow_submit")
        val allowSubmit: Boolean,
        @SerialName("lan")
        val lan: String,
        @SerialName("lan_doc")
        val lanDoc: String,
    )

    @Serializable
    data class Vip(
        @SerialName("avatar_icon")
        val avatarIcon: AvatarIcon,
        @SerialName("avatar_subscript")
        val avatarSubscript: Int,
        @SerialName("avatar_subscript_url")
        val avatarSubscriptUrl: String,
        @SerialName("due_date")
        val dueDate: Long,
        @SerialName("label")
        val label: Label,
        @SerialName("nickname_color")
        val nicknameColor: String,
        @SerialName("ott_info")
        val ottInfo: OttInfo,
        @SerialName("role")
        val role: Int,
        @SerialName("status")
        val status: Int,
        @SerialName("super_vip")
        val superVip: SuperVip,
        @SerialName("theme_type")
        val themeType: Int,
        @SerialName("tv_due_date")
        val tvDueDate: Int,
        @SerialName("tv_vip_pay_type")
        val tvVipPayType: Int,
        @SerialName("tv_vip_status")
        val tvVipStatus: Int,
        @SerialName("type")
        val type: Int,
        @SerialName("vip_pay_type")
        val vipPayType: Int
    ) {
        @Serializable
        data class AvatarIcon(
            @SerialName("icon_resource")
            val iconResource: IconResource
        ) {
            @Serializable
            class IconResource
        }

        @Serializable
        data class Label(
            @SerialName("bg_color")
            val bgColor: String,
            @SerialName("bg_style")
            val bgStyle: Int,
            @SerialName("border_color")
            val borderColor: String,
            @SerialName("img_label_uri_hans")
            val imgLabelUriHans: String,
            @SerialName("img_label_uri_hans_static")
            val imgLabelUriHansStatic: String,
            @SerialName("img_label_uri_hant")
            val imgLabelUriHant: String,
            @SerialName("img_label_uri_hant_static")
            val imgLabelUriHantStatic: String,
            @SerialName("label_id")
            val labelId: Int,
            @SerialName("label_theme")
            val labelTheme: String,
            @SerialName("path")
            val path: String,
            @SerialName("text")
            val text: String,
            @SerialName("text_color")
            val textColor: String,
            @SerialName("use_img_label")
            val useImgLabel: Boolean
        )

        @Serializable
        data class OttInfo(
            @SerialName("overdue_time")
            val overdueTime: Int,
            @SerialName("pay_channel_id")
            val payChannelId: String,
            @SerialName("pay_type")
            val payType: Int,
            @SerialName("status")
            val status: Int,
            @SerialName("vip_type")
            val vipType: Int
        )

        @Serializable
        data class SuperVip(
            @SerialName("is_super_vip")
            val isSuperVip: Boolean
        )
    }
}