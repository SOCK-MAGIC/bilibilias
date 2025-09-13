package com.imcys.bilibilias.core.datasource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Season(
    @SerialName("activity")
    val activity: Activity,
    @SerialName("actors")
    val actors: String,
    @SerialName("alias")
    val alias: String,
    @SerialName("areas")
    val areas: List<Area>,
    @SerialName("bkg_cover")
    val bkgCover: String,
    @SerialName("cover")
    val cover: String,
    @SerialName("delivery_fragment_video")
    val deliveryFragmentVideo: Boolean,
    @SerialName("enable_vt")
    val enableVt: Boolean,
    @SerialName("episodes")
    val episodes: List<Episode>,
    @SerialName("evaluate")
    val evaluate: String,
    @SerialName("freya")
    val freya: Freya,
    @SerialName("hide_ep_vv_vt_dm")
    val hideEpVvVtDm: Int,
    @SerialName("icon_font")
    val iconFont: IconFont,
    @SerialName("jp_title")
    val jpTitle: String,
    @SerialName("link")
    val link: String,
    @SerialName("media_id")
    val mediaId: Int,
    @SerialName("mode")
    val mode: Int,
    @SerialName("new_ep")
    val newEp: NewEp,
    @SerialName("payment")
    val payment: Payment,
    @SerialName("play_strategy")
    val playStrategy: PlayStrategy,
    @SerialName("positive")
    val positive: Positive,
    @SerialName("publish")
    val publish: Publish,
    @SerialName("rating")
    val rating: Rating,
    @SerialName("record")
    val record: String,
    @SerialName("rights")
    val rights: Rights,
    @SerialName("season_id")
    val seasonId: Int,
    @SerialName("season_title")
    val seasonTitle: String,
    @SerialName("seasons")
    val seasons: List<Season>,
    @SerialName("section")
    val section: List<Section>,
    @SerialName("series")
    val series: Series,
    @SerialName("share_copy")
    val shareCopy: String,
    @SerialName("share_sub_title")
    val shareSubTitle: String,
    @SerialName("share_url")
    val shareUrl: String,
    @SerialName("show")
    val show: Show,
    @SerialName("show_season_type")
    val showSeasonType: Int,
    @SerialName("square_cover")
    val squareCover: String,
    @SerialName("staff")
    val staff: String,
    @SerialName("stat")
    val stat: Stat,
    @SerialName("status")
    val status: Int,
    @SerialName("styles")
    val styles: List<String>,
    @SerialName("subtitle")
    val subtitle: String,
    @SerialName("title")
    val title: String,
    @SerialName("total")
    val total: Int,
    @SerialName("type")
    val type: Int,
    @SerialName("up_info")
    val upInfo: UpInfo,
    @SerialName("user_status")
    val userStatus: UserStatus
) {
    @Serializable
    data class Activity(
        @SerialName("head_bg_url")
        val headBgUrl: String,
        @SerialName("id")
        val id: Int,
        @SerialName("title")
        val title: String
    )

    @Serializable
    data class Area(
        @SerialName("id")
        val id: Int,
        @SerialName("name")
        val name: String
    )

    @Serializable
    data class Episode(
        @SerialName("aid")
        val aid: Long,
        @SerialName("badge")
        val badge: String,
        @SerialName("badge_info")
        val badgeInfo: BadgeInfo,
        @SerialName("badge_type")
        val badgeType: Int,
        @SerialName("bvid")
        val bvid: String,
        @SerialName("cid")
        val cid: Long,
        @SerialName("cover")
        val cover: String,
        @SerialName("dimension")
        val dimension: Dimension,
        @SerialName("duration")
        val duration: Int,
        @SerialName("enable_vt")
        val enableVt: Boolean,
        @SerialName("ep_id")
        val epId: Int,
        @SerialName("from")
        val from: String,
        @SerialName("id")
        val id: Int,
        @SerialName("is_view_hide")
        val isViewHide: Boolean,
        @SerialName("link")
        val link: String,
        @SerialName("long_title")
        val longTitle: String,
        @SerialName("pub_time")
        val pubTime: Int,
        @SerialName("pv")
        val pv: Int,
        @SerialName("release_date")
        val releaseDate: String,
        @SerialName("rights")
        val rights: Rights,
        @SerialName("section_type")
        val sectionType: Int,
        @SerialName("share_copy")
        val shareCopy: String,
        @SerialName("share_url")
        val shareUrl: String,
        @SerialName("short_link")
        val shortLink: String,
        @SerialName("showDrmLoginDialog")
        val showDrmLoginDialog: Boolean,
        @SerialName("show_title")
        val showTitle: String,
        @SerialName("skip")
        val skip: Skip? = null,
        @SerialName("status")
        val status: Int,
        @SerialName("subtitle")
        val subtitle: String,
        @SerialName("title")
        val title: String,
        @SerialName("vid")
        val vid: String
    ) {
        @Serializable
        data class BadgeInfo(
            @SerialName("bg_color")
            val bgColor: String,
            @SerialName("bg_color_night")
            val bgColorNight: String,
            @SerialName("text")
            val text: String
        )

        @Serializable
        data class Dimension(
            @SerialName("height")
            val height: Int,
            @SerialName("rotate")
            val rotate: Int,
            @SerialName("width")
            val width: Int
        )

        @Serializable
        data class Rights(
            @SerialName("allow_dm")
            val allowDm: Int,
            @SerialName("allow_download")
            val allowDownload: Int,
            @SerialName("area_limit")
            val areaLimit: Int
        )

        @Serializable
        data class Skip(
            @SerialName("ed")
            val ed: Ed,
            @SerialName("op")
            val op: Op
        ) {
            @Serializable
            data class Ed(
                @SerialName("end")
                val end: Int,
                @SerialName("start")
                val start: Int
            )

            @Serializable
            data class Op(
                @SerialName("end")
                val end: Int,
                @SerialName("start")
                val start: Int
            )
        }
    }

    @Serializable
    data class Freya(
        @SerialName("bubble_show_cnt")
        val bubbleShowCnt: Int,
        @SerialName("icon_show")
        val iconShow: Int
    )

    @Serializable
    data class IconFont(
        @SerialName("name")
        val name: String,
        @SerialName("text")
        val text: String
    )

    @Serializable
    data class NewEp(
        @SerialName("desc")
        val desc: String,
        @SerialName("id")
        val id: Int,
        @SerialName("is_new")
        val isNew: Int,
        @SerialName("title")
        val title: String
    )

    @Serializable
    data class Payment(
        @SerialName("discount")
        val discount: Int,
        @SerialName("pay_type")
        val payType: PayType,
        @SerialName("price")
        val price: String,
        @SerialName("promotion")
        val promotion: String,
        @SerialName("tip")
        val tip: String,
        @SerialName("view_start_time")
        val viewStartTime: Int,
        @SerialName("vip_discount")
        val vipDiscount: Int,
        @SerialName("vip_first_promotion")
        val vipFirstPromotion: String,
        @SerialName("vip_price")
        val vipPrice: String,
        @SerialName("vip_promotion")
        val vipPromotion: String
    ) {
        @Serializable
        data class PayType(
            @SerialName("allow_discount")
            val allowDiscount: Int,
            @SerialName("allow_pack")
            val allowPack: Int,
            @SerialName("allow_ticket")
            val allowTicket: Int,
            @SerialName("allow_time_limit")
            val allowTimeLimit: Int,
            @SerialName("allow_vip_discount")
            val allowVipDiscount: Int,
            @SerialName("forbid_bb")
            val forbidBb: Int
        )
    }

    @Serializable
    data class PlayStrategy(
        @SerialName("strategies")
        val strategies: List<String>
    )

    @Serializable
    data class Positive(
        @SerialName("id")
        val id: Int,
        @SerialName("title")
        val title: String
    )

    @Serializable
    data class Publish(
        @SerialName("is_finish")
        val isFinish: Int,
        @SerialName("is_started")
        val isStarted: Int,
        @SerialName("pub_time")
        val pubTime: String,
        @SerialName("pub_time_show")
        val pubTimeShow: String,
        @SerialName("unknow_pub_date")
        val unknowPubDate: Int,
        @SerialName("weekday")
        val weekday: Int
    )

    @Serializable
    data class Rating(
        @SerialName("count")
        val count: Int,
        @SerialName("score")
        val score: Double
    )

    @Serializable
    data class Rights(
        @SerialName("allow_bp")
        val allowBp: Int,
        @SerialName("allow_bp_rank")
        val allowBpRank: Int,
        @SerialName("allow_download")
        val allowDownload: Int,
        @SerialName("allow_review")
        val allowReview: Int,
        @SerialName("area_limit")
        val areaLimit: Int,
        @SerialName("ban_area_show")
        val banAreaShow: Int,
        @SerialName("can_watch")
        val canWatch: Int,
        @SerialName("copyright")
        val copyright: String,
        @SerialName("forbid_pre")
        val forbidPre: Int,
        @SerialName("freya_white")
        val freyaWhite: Int,
        @SerialName("is_cover_show")
        val isCoverShow: Int,
        @SerialName("is_preview")
        val isPreview: Int,
        @SerialName("is_sponsor")
        val isSponsor: Int,
        @SerialName("only_vip_download")
        val onlyVipDownload: Int,
        @SerialName("resource")
        val resource: String,
        @SerialName("watch_platform")
        val watchPlatform: Int
    )

    @Serializable
    data class Season(
        @SerialName("badge")
        val badge: String,
        @SerialName("badge_info")
        val badgeInfo: BadgeInfo,
        @SerialName("badge_type")
        val badgeType: Int,
        @SerialName("cover")
        val cover: String,
        @SerialName("enable_vt")
        val enableVt: Boolean,
        @SerialName("horizontal_cover_1610")
        val horizontalCover1610: String,
        @SerialName("horizontal_cover_169")
        val horizontalCover169: String,
        @SerialName("icon_font")
        val iconFont: IconFont,
        @SerialName("media_id")
        val mediaId: Int,
        @SerialName("new_ep")
        val newEp: NewEp,
        @SerialName("season_id")
        val seasonId: Int,
        @SerialName("season_title")
        val seasonTitle: String,
        @SerialName("season_type")
        val seasonType: Int,
        @SerialName("stat")
        val stat: Stat
    ) {
        @Serializable
        data class BadgeInfo(
            @SerialName("bg_color")
            val bgColor: String,
            @SerialName("bg_color_night")
            val bgColorNight: String,
            @SerialName("text")
            val text: String
        )

        @Serializable
        data class IconFont(
            @SerialName("name")
            val name: String,
            @SerialName("text")
            val text: String
        )

        @Serializable
        data class NewEp(
            @SerialName("cover")
            val cover: String,
            @SerialName("id")
            val id: Int,
            @SerialName("index_show")
            val indexShow: String
        )

        @Serializable
        data class Stat(
            @SerialName("favorites")
            val favorites: Int,
            @SerialName("series_follow")
            val seriesFollow: Int,
            @SerialName("views")
            val views: Int,
            @SerialName("vt")
            val vt: Int
        )
    }

    @Serializable
    data class Section(
        @SerialName("attr")
        val attr: Int,
        @SerialName("episode_id")
        val episodeId: Int,
        @SerialName("episodes")
        val episodes: List<Episode>,
        @SerialName("id")
        val id: Int,
        @SerialName("report")
        val report: Report? = null,
        @SerialName("title")
        val title: String,
        @SerialName("type")
        val type: Int,
        @SerialName("type2")
        val type2: Int
    ) {
        @Serializable
        data class Episode(
            @SerialName("aid")
            val aid: Long,
            @SerialName("badge")
            val badge: String,
            @SerialName("badge_info")
            val badgeInfo: BadgeInfo,
            @SerialName("badge_type")
            val badgeType: Int? = null,
            @SerialName("bvid")
            val bvid: String? = null,
            @SerialName("cid")
            val cid: Long,
            @SerialName("cover")
            val cover: String,
            @SerialName("dimension")
            val dimension: Dimension? = null,
            @SerialName("duration")
            val duration: Int? = null,
            @SerialName("enable_vt")
            val enableVt: Boolean,
            @SerialName("ep_id")
            val epId: Int,
            @SerialName("from")
            val from: String? = null,
            @SerialName("icon_font")
            val iconFont: IconFont? = null,
            @SerialName("id")
            val id: Int,
            @SerialName("is_view_hide")
            val isViewHide: Boolean,
            @SerialName("link")
            val link: String,
            @SerialName("link_type")
            val linkType: String? = null,
            @SerialName("long_title")
            val longTitle: String? = null,
            @SerialName("pub_time")
            val pubTime: Int,
            @SerialName("pv")
            val pv: Int,
            @SerialName("release_date")
            val releaseDate: String? = null,
            @SerialName("report")
            val report: Report? = null,
            @SerialName("rights")
            val rights: Rights? = null,
            @SerialName("section_type")
            val sectionType: Int,
            @SerialName("share_copy")
            val shareCopy: String? = null,
            @SerialName("share_url")
            val shareUrl: String? = null,
            @SerialName("short_link")
            val shortLink: String? = null,
            @SerialName("showDrmLoginDialog")
            val showDrmLoginDialog: Boolean,
            @SerialName("show_title")
            val showTitle: String? = null,
            @SerialName("skip")
            val skip: Skip? = null,
            @SerialName("stat")
            val stat: Stat? = null,
            @SerialName("stat_for_unity")
            val statForUnity: StatForUnity? = null,
            @SerialName("status")
            val status: Int,
            @SerialName("subtitle")
            val subtitle: String? = null,
            @SerialName("title")
            val title: String,
            @SerialName("vid")
            val vid: String? = null
        ) {
            @Serializable
            data class BadgeInfo(
                @SerialName("bg_color")
                val bgColor: String,
                @SerialName("bg_color_night")
                val bgColorNight: String,
                @SerialName("text")
                val text: String
            )

            @Serializable
            data class Dimension(
                @SerialName("height")
                val height: Int,
                @SerialName("rotate")
                val rotate: Int,
                @SerialName("width")
                val width: Int
            )

            @Serializable
            data class IconFont(
                @SerialName("name")
                val name: String,
                @SerialName("text")
                val text: String
            )

            @Serializable
            data class Report(
                @SerialName("aid")
                val aid: String,
                @SerialName("ep_title")
                val epTitle: String,
                @SerialName("position")
                val position: String,
                @SerialName("season_id")
                val seasonId: String,
                @SerialName("season_type")
                val seasonType: String,
                @SerialName("section_id")
                val sectionId: String,
                @SerialName("section_type")
                val sectionType: String
            )

            @Serializable
            data class Rights(
                @SerialName("allow_dm")
                val allowDm: Int,
                @SerialName("allow_download")
                val allowDownload: Int,
                @SerialName("area_limit")
                val areaLimit: Int
            )

            @Serializable
            data class Skip(
                @SerialName("ed")
                val ed: Ed,
                @SerialName("op")
                val op: Op
            ) {
                @Serializable
                data class Ed(
                    @SerialName("end")
                    val end: Int,
                    @SerialName("start")
                    val start: Int
                )

                @Serializable
                data class Op(
                    @SerialName("end")
                    val end: Int,
                    @SerialName("start")
                    val start: Int
                )
            }

            @Serializable
            data class Stat(
                @SerialName("coin")
                val coin: Int,
                @SerialName("danmakus")
                val danmakus: Int,
                @SerialName("likes")
                val likes: Int,
                @SerialName("play")
                val play: Int,
                @SerialName("reply")
                val reply: Int,
                @SerialName("vt")
                val vt: Int
            )

            @Serializable
            data class StatForUnity(
                @SerialName("coin")
                val coin: Int,
                @SerialName("danmaku")
                val danmaku: Danmaku,
                @SerialName("likes")
                val likes: Int,
                @SerialName("reply")
                val reply: Int,
                @SerialName("vt")
                val vt: Vt
            ) {
                @Serializable
                data class Danmaku(
                    @SerialName("icon")
                    val icon: String,
                    @SerialName("pure_text")
                    val pureText: String,
                    @SerialName("text")
                    val text: String,
                    @SerialName("value")
                    val value: Int
                )

                @Serializable
                data class Vt(
                    @SerialName("icon")
                    val icon: String,
                    @SerialName("pure_text")
                    val pureText: String,
                    @SerialName("text")
                    val text: String,
                    @SerialName("value")
                    val value: Int
                )
            }
        }

        @Serializable
        data class Report(
            @SerialName("season_id")
            val seasonId: String,
            @SerialName("season_type")
            val seasonType: String,
            @SerialName("sec_title")
            val secTitle: String,
            @SerialName("section_id")
            val sectionId: String,
            @SerialName("section_type")
            val sectionType: String
        )
    }

    @Serializable
    data class Series(
        @SerialName("display_type")
        val displayType: Int,
        @SerialName("series_id")
        val seriesId: Int,
        @SerialName("series_title")
        val seriesTitle: String
    )

    @Serializable
    data class Show(
        @SerialName("wide_screen")
        val wideScreen: Int
    )

    @Serializable
    data class Stat(
        @SerialName("coins")
        val coins: Int,
        @SerialName("danmakus")
        val danmakus: Int,
        @SerialName("favorite")
        val favorite: Int,
        @SerialName("favorites")
        val favorites: Int,
        @SerialName("follow_text")
        val followText: String,
        @SerialName("likes")
        val likes: Int,
        @SerialName("reply")
        val reply: Int,
        @SerialName("share")
        val share: Int,
        @SerialName("views")
        val views: Int,
        @SerialName("vt")
        val vt: Int
    )

    @Serializable
    data class UpInfo(
        @SerialName("avatar")
        val avatar: String,
        @SerialName("avatar_subscript_url")
        val avatarSubscriptUrl: String,
        @SerialName("follower")
        val follower: Int,
        @SerialName("is_follow")
        val isFollow: Int,
        @SerialName("mid")
        val mid: Int,
        @SerialName("nickname_color")
        val nicknameColor: String,
        @SerialName("pendant")
        val pendant: Pendant,
        @SerialName("theme_type")
        val themeType: Int,
        @SerialName("uname")
        val uname: String,
        @SerialName("verify_type")
        val verifyType: Int,
        @SerialName("vip_label")
        val vipLabel: VipLabel,
        @SerialName("vip_status")
        val vipStatus: Int,
        @SerialName("vip_type")
        val vipType: Int
    ) {
        @Serializable
        data class Pendant(
            @SerialName("image")
            val image: String,
            @SerialName("name")
            val name: String,
            @SerialName("pid")
            val pid: Int
        )

        @Serializable
        data class VipLabel(
            @SerialName("bg_color")
            val bgColor: String,
            @SerialName("bg_style")
            val bgStyle: Int,
            @SerialName("border_color")
            val borderColor: String,
            @SerialName("text")
            val text: String,
            @SerialName("text_color")
            val textColor: String
        )
    }

    @Serializable
    data class UserStatus(
        @SerialName("area_limit")
        val areaLimit: Int,
        @SerialName("ban_area_show")
        val banAreaShow: Int,
        @SerialName("follow")
        val follow: Int,
        @SerialName("follow_status")
        val followStatus: Int,
        @SerialName("login")
        val login: Int,
        @SerialName("pay")
        val pay: Int,
        @SerialName("pay_pack_paid")
        val payPackPaid: Int,
        @SerialName("sponsor")
        val sponsor: Int
    )
}