package com.imcys.bilibilias.core.datasource.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PgcPlayUrl(
    @SerialName("exp_info")
    val expInfo: ExpInfo,
    @SerialName("play_check")
    val playCheck: PlayCheck,
    @SerialName("play_view_business_info")
    val playViewBusinessInfo: PlayViewBusinessInfo,
    @SerialName("video_info")
    val videoInfo: VideoInfo,
    @SerialName("view_info")
    val viewInfo: ViewInfo
) {
    @Serializable
    data class ExpInfo(
        @SerialName("buy_vip_donated_season")
        val buyVipDonatedSeason: Int
    )

    @Serializable
    data class PlayCheck(
        @SerialName("play_detail")
        val playDetail: String
    )

    @Serializable
    data class PlayViewBusinessInfo(
        @SerialName("episode_info")
        val episodeInfo: EpisodeInfo,
        @SerialName("season_info")
        val seasonInfo: SeasonInfo,
        @SerialName("user_status")
        val userStatus: UserStatus
    ) {
        @Serializable
        data class EpisodeInfo(
            @SerialName("aid")
            val aid: Long,
            @SerialName("bvid")
            val bvid: String,
            @SerialName("cid")
            val cid: Long,
            @SerialName("delivery_business_fragment_video")
            val deliveryBusinessFragmentVideo: Boolean,
            @SerialName("delivery_fragment_video")
            val deliveryFragmentVideo: Boolean,
            @SerialName("ep_id")
            val epId: Int,
            @SerialName("ep_status")
            val epStatus: Int,
            @SerialName("interaction")
            val interaction: Interaction,
            @SerialName("long_title")
            val longTitle: String,
            @SerialName("title")
            val title: String
        ) {
            @Serializable
            data class Interaction(
                @SerialName("interaction")
                val interaction: Boolean
            )
        }

        @Serializable
        data class SeasonInfo(
            @SerialName("season_id")
            val seasonId: Int,
            @SerialName("season_type")
            val seasonType: Int
        )

        @Serializable
        data class UserStatus(
            @SerialName("follow_info")
            val followInfo: FollowInfo,
            @SerialName("is_login")
            val isLogin: Int,
            @SerialName("pay_info")
            val payInfo: PayInfo,
            @SerialName("vip_info")
            val vipInfo: VipInfo,
            @SerialName("watch_progress")
            val watchProgress: WatchProgress
        ) {
            @Serializable
            data class FollowInfo(
                @SerialName("follow")
                val follow: Int,
                @SerialName("follow_status")
                val followStatus: Int
            )

            @Serializable
            data class PayInfo(
                @SerialName("pay_check")
                val payCheck: Int,
                @SerialName("pay_pack_paid")
                val payPackPaid: Int,
                @SerialName("sponsor")
                val sponsor: Int
            )

            @Serializable
            data class VipInfo(
                @SerialName("real_vip")
                val realVip: Boolean
            )

            @Serializable
            data class WatchProgress(
                @SerialName("current_watch_progress")
                val currentWatchProgress: Int,
                @SerialName("last_ep_id")
                val lastEpId: Int,
                @SerialName("last_time")
                val lastTime: Int
            )
        }
    }

    @Serializable
    data class VideoInfo(
        @SerialName("accept_description")
        val acceptDescription: List<String>,
        @SerialName("accept_format")
        val acceptFormat: String,
        @SerialName("accept_quality")
        val acceptQuality: List<Int>,
        @SerialName("bp")
        val bp: Int,
        @SerialName("clip_info_list")
        val clipInfoList: List<ClipInfo>,
        @SerialName("code")
        val code: Int,
        @SerialName("dash")
        val dash: Dash,
        @SerialName("durls")
        val durls: List<String>? = null,
        @SerialName("error_code")
        val errorCode: Int,
        @SerialName("fnval")
        val fnval: Int,
        @SerialName("fnver")
        val fnver: Int,
        @SerialName("format")
        val format: String,
        @SerialName("from")
        val from: String,
        @SerialName("has_paid")
        val hasPaid: Boolean,
        @SerialName("is_drm")
        val isDrm: Boolean,
        @SerialName("is_preview")
        val isPreview: Int,
        @SerialName("message")
        val message: String,
        @SerialName("no_rexcode")
        val noRexcode: Int,
        @SerialName("quality")
        val quality: Int,
        @SerialName("record_info")
        val recordInfo: RecordInfo,
        @SerialName("result")
        val result: String,
        @SerialName("seek_param")
        val seekParam: String,
        @SerialName("seek_type")
        val seekType: String,
        @SerialName("status")
        val status: Int,
        @SerialName("support_formats")
        val supportFormats: List<SupportFormat>,
        @SerialName("timelength")
        val timelength: Int,
        @SerialName("type")
        val type: String,
        @SerialName("video_codecid")
        val videoCodecid: Int,
        @SerialName("video_project")
        val videoProject: Boolean
    ) {
        @Serializable
        data class ClipInfo(
            @SerialName("clipType")
            val clipType: String,
            @SerialName("end")
            val end: Int,
            @SerialName("materialNo")
            val materialNo: Int,
            @SerialName("start")
            val start: Int,
            @SerialName("toastText")
            val toastText: String
        )

        @Serializable
        data class RecordInfo(
            @SerialName("record")
            val record: String,
            @SerialName("record_icon")
            val recordIcon: String
        )

        @Serializable
        data class SupportFormat(
            @SerialName("attribute")
            val attribute: Int,
            @SerialName("codecs")
            val codecs: List<String>,
            @SerialName("description")
            val description: String,
            @SerialName("display_desc")
            val displayDesc: String,
            @SerialName("format")
            val format: String,
            @SerialName("has_preview")
            val hasPreview: Boolean,
            @SerialName("need_login")
            val needLogin: Boolean = false,
            @SerialName("need_vip")
            val needVip: Boolean = false,
            @SerialName("new_description")
            val newDescription: String,
            @SerialName("quality")
            val quality: Int,
            @SerialName("sub_description")
            val subDescription: String,
            @SerialName("superscript")
            val superscript: String
        )
    }

    @Serializable
    data class ViewInfo(
        @SerialName("ai_repair_qn_trial_info")
        val aiRepairQnTrialInfo: AiRepairQnTrialInfo,
        @SerialName("qn_trial_info")
        val qnTrialInfo: QnTrialInfo,
        @SerialName("report")
        val report: Report
    ) {
        @Serializable
        data class AiRepairQnTrialInfo(
            @SerialName("trial_able")
            val trialAble: Boolean
        )

        @Serializable
        data class EndPage(
            @SerialName("hide")
            val hide: Boolean
        )

        @Serializable
        data class QnTrialInfo(
            @SerialName("trial_able")
            val trialAble: Boolean
        )

        @Serializable
        data class Report(
            @SerialName("ep_id")
            val epId: String,
            @SerialName("ep_status")
            val epStatus: String,
            @SerialName("season_id")
            val seasonId: String,
            @SerialName("season_status")
            val seasonStatus: String,
            @SerialName("season_type")
            val seasonType: String,
            @SerialName("vip_status")
            val vipStatus: String,
            @SerialName("vip_type")
            val vipType: String
        )
    }
}