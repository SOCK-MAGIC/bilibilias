package com.imcys.bilibilias.core.datasource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheeseInfoData(
    @SerialName("abtest_info")
    val abtestInfo: AbtestInfo,
    @SerialName("be_subscription")
    val beSubscription: Boolean,
    @SerialName("brief")
    val brief: Brief,
    @SerialName("consulting")
    val consulting: Consulting,
    @SerialName("cooperation")
    val cooperation: Cooperation,
    @SerialName("course_content")
    val courseContent: String,
    @SerialName("cover")
    val cover: String,
    @SerialName("ep_count")
    val epCount: Int,
    @SerialName("episode_page")
    val episodePage: EpisodePage,
    @SerialName("episode_sort")
    val episodeSort: Int,
    @SerialName("episode_tag")
    val episodeTag: EpisodeTag,
    @SerialName("episodes")
    val episodes: List<Episode>,
    @SerialName("expiry_day")
    val expiryDay: Int,
    @SerialName("expiry_info_content")
    val expiryInfoContent: String,
    @SerialName("faq")
    val faq: Faq,
    @SerialName("faq1")
    val faq1: Faq1,
    @SerialName("is_enable_cash")
    val isEnableCash: Boolean,
    @SerialName("is_series")
    val isSeries: Boolean,
    @SerialName("live_ep_count")
    val liveEpCount: Int,
    @SerialName("opened_ep_count")
    val openedEpCount: Int,
    @SerialName("pack_info")
    val packInfo: PackInfo,
    @SerialName("paid_jump")
    val paidJump: PaidJump,
    @SerialName("paid_view")
    val paidView: Boolean,
    @SerialName("payment")
    val payment: Payment,
    @SerialName("previewed_purchase_note")
    val previewedPurchaseNote: PreviewedPurchaseNote,
    @SerialName("purchase_format_note")
    val purchaseFormatNote: PurchaseFormatNote,
    @SerialName("purchase_note")
    val purchaseNote: PurchaseNote,
    @SerialName("purchase_protocol")
    val purchaseProtocol: PurchaseProtocol,
    @SerialName("recommend_seasons")
    val recommendSeasons: List<RecommendSeason>,
    @SerialName("release_bottom_info")
    val releaseBottomInfo: String,
    @SerialName("release_info")
    val releaseInfo: String,
    @SerialName("release_info2")
    val releaseInfo2: String,
    @SerialName("release_status")
    val releaseStatus: String,
    @SerialName("season_id")
    val seasonId: Int,
    @SerialName("season_tag")
    val seasonTag: Int,
    @SerialName("share_url")
    val shareUrl: String,
    @SerialName("short_link")
    val shortLink: String,
    @SerialName("show_watermark")
    val showWatermark: Boolean,
    @SerialName("stat")
    val stat: Stat,
    @SerialName("status")
    val status: Int,
    @SerialName("stop_sell")
    val stopSell: Boolean,
    @SerialName("subscription_update_count_cycle_text")
    val subscriptionUpdateCountCycleText: String,
    @SerialName("subtitle")
    val subtitle: String,
    @SerialName("title")
    val title: String,
    @SerialName("up_info")
    val upInfo: UpInfo,
    @SerialName("update_status")
    val updateStatus: Int,
    @SerialName("user_status")
    val userStatus: UserStatus,
    @SerialName("watermark_interval")
    val watermarkInterval: Int
) {
    @Serializable
    data class AbtestInfo(
        @SerialName("style_abtest")
        val styleAbtest: Int
    )

    @Serializable
    data class Brief(
        @SerialName("content")
        val content: String,
        @SerialName("title")
        val title: String,
        @SerialName("type")
        val type: Int
    )

    @Serializable
    data class Consulting(
        @SerialName("consulting_flag")
        val consultingFlag: Boolean,
        @SerialName("consulting_url")
        val consultingUrl: String
    )

    @Serializable
    data class Cooperation(
        @SerialName("link")
        val link: String
    )

    @Serializable
    data class EpisodePage(
        @SerialName("next")
        val next: Boolean,
        @SerialName("num")
        val num: Int,
        @SerialName("size")
        val size: Int,
        @SerialName("total")
        val total: Int
    )

    @Serializable
    data class EpisodeTag(
        @SerialName("part_preview_tag")
        val partPreviewTag: String,
        @SerialName("pay_tag")
        val payTag: String,
        @SerialName("preview_tag")
        val previewTag: String
    )

    @Serializable
    data class Episode(
        @SerialName("aid")
        val aid: Long,
        @SerialName("catalogue_index")
        val catalogueIndex: Int,
        @SerialName("cid")
        val cid: Long,
        @SerialName("cover")
        val cover: String,
        @SerialName("duration")
        val duration: Int,
        @SerialName("ep_status")
        val epStatus: Int,
        @SerialName("episode_can_view")
        val episodeCanView: Boolean,
        @SerialName("from")
        val from: String,
        @SerialName("id")
        val id: Int,
        @SerialName("index")
        val index: Int,
        @SerialName("label")
        val label: String?,
        @SerialName("page")
        val page: Int,
        @SerialName("play")
        val play: Int,
        @SerialName("play_way")
        val playWay: Int,
        @SerialName("playable")
        val playable: Boolean,
        @SerialName("release_date")
        val releaseDate: Int,
        @SerialName("show_vt")
        val showVt: Boolean,
        @SerialName("status")
        val status: Int,
        @SerialName("subtitle")
        val subtitle: String,
        @SerialName("title")
        val title: String,
        @SerialName("watched")
        val watched: Boolean,
        @SerialName("watchedHistory")
        val watchedHistory: Int
    )

    @Serializable
    data class Faq(
        @SerialName("content")
        val content: String,
        @SerialName("link")
        val link: String,
        @SerialName("title")
        val title: String
    )

    @Serializable
    data class Faq1(
        @SerialName("items")
        val items: List<Item>,
        @SerialName("title")
        val title: String
    ) {
        @Serializable
        data class Item(
            @SerialName("answer")
            val answer: String,
            @SerialName("question")
            val question: String
        )
    }

    @Serializable
    data class PackInfo(
        @SerialName("pack_notice2")
        val packNotice2: String,
        @SerialName("show_packs_right")
        val showPacksRight: Boolean,
        @SerialName("title")
        val title: String
    )

    @Serializable
    data class PaidJump(
        @SerialName("jump_url_for_app")
        val jumpUrlForApp: String,
        @SerialName("url")
        val url: String
    )

    @Serializable
    data class Payment(
        @SerialName("bp_enough")
        val bpEnough: Int,
        @SerialName("desc")
        val desc: String,
        @SerialName("my_bp")
        val myBp: Int,
        @SerialName("pay_shade")
        val payShade: String,
        @SerialName("price")
        val price: Double,
        @SerialName("price_format")
        val priceFormat: String,
        @SerialName("price_unit")
        val priceUnit: String,
        @SerialName("refresh_text")
        val refreshText: String,
        @SerialName("select_text")
        val selectText: String
    )

    @Serializable
    data class PreviewedPurchaseNote(
        @SerialName("long_watch_text")
        val longWatchText: String,
        @SerialName("pay_text")
        val payText: String,
        @SerialName("price_format")
        val priceFormat: String,
        @SerialName("watch_text")
        val watchText: String,
        @SerialName("watching_text")
        val watchingText: String
    )

    @Serializable
    data class PurchaseFormatNote(
        @SerialName("content_list")
        val contentList: List<Content>,
        @SerialName("link")
        val link: String,
        @SerialName("title")
        val title: String
    ) {
        @Serializable
        data class Content(
            @SerialName("bold")
            val bold: Boolean,
            @SerialName("content")
            val content: String,
            @SerialName("number")
            val number: String
        )
    }

    @Serializable
    data class PurchaseNote(
        @SerialName("content")
        val content: String,
        @SerialName("link")
        val link: String,
        @SerialName("title")
        val title: String
    )

    @Serializable
    data class PurchaseProtocol(
        @SerialName("link")
        val link: String,
        @SerialName("title")
        val title: String
    )

    @Serializable
    data class RecommendSeason(
        @SerialName("cover")
        val cover: String,
        @SerialName("ep_count")
        val epCount: String,
        @SerialName("id")
        val id: Int,
        @SerialName("season_url")
        val seasonUrl: String,
        @SerialName("subtitle")
        val subtitle: String,
        @SerialName("title")
        val title: String,
        @SerialName("view")
        val view: Int
    )

    @Serializable
    data class Stat(
        @SerialName("play")
        val play: Int,
        @SerialName("play_desc")
        val playDesc: String,
        @SerialName("show_vt")
        val showVt: Boolean
    )

    @Serializable
    data class UpInfo(
        @SerialName("avatar")
        val avatar: String,
        @SerialName("brief")
        val brief: String,
        @SerialName("follower")
        val follower: Int,
        @SerialName("is_follow")
        val isFollow: Int,
        @SerialName("is_living")
        val isLiving: Boolean,
        @SerialName("link")
        val link: String,
        @SerialName("mid")
        val mid: Int,
        @SerialName("pendant")
        val pendant: Pendant,
        @SerialName("season_count")
        val seasonCount: Int,
        @SerialName("uname")
        val uname: String
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
    }

    @Serializable
    data class UserStatus(
        @SerialName("bp")
        val bp: Int,
        @SerialName("expire_at")
        val expireAt: Int,
        @SerialName("favored")
        val favored: Int,
        @SerialName("favored_count")
        val favoredCount: Int,
        @SerialName("is_expired")
        val isExpired: Boolean,
        @SerialName("is_first_paid")
        val isFirstPaid: Boolean,
        @SerialName("payed")
        val payed: Int,
        @SerialName("user_expiry_content")
        val userExpiryContent: String
    )
}