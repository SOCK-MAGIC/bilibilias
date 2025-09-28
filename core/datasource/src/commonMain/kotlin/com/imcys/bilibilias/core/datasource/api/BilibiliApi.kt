package com.imcys.bilibilias.core.datasource.api

import com.imcys.bilibilias.core.datasource.model.BiliVideoData
import com.imcys.bilibilias.core.datasource.model.BilibiliNavigationData
import com.imcys.bilibilias.core.datasource.model.DmSegMobileReply
import com.imcys.bilibilias.core.datasource.model.InteractiveChoiceDetails
import com.imcys.bilibilias.core.datasource.model.PgcPlayUrl
import com.imcys.bilibilias.core.datasource.model.PlayerInfo
import com.imcys.bilibilias.core.datasource.model.Season
import com.imcys.bilibilias.core.datasource.model.UgcPlayUrl
import com.imcys.bilibilias.core.datasource.model.UserProfile
import com.imcys.bilibilias.core.datasource.utils.WbiSign
import com.imcys.bilibilias.core.datastore.AsPreferencesDataSource
import com.imcys.bilibilias.core.datastore.CookieJarDataSource
import com.imcys.bilibilias.core.logging.Logger
import com.imcys.bilibilias.core.logging.logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.request
import io.ktor.http.CookieEncoding
import io.ktor.http.HttpHeaders
import io.ktor.http.decodeCookieValue
import io.ktor.http.parseClientCookiesHeader
import io.ktor.http.parseQueryString
import io.ktor.http.renderSetCookieHeader
import kotlinx.coroutines.flow.first

class BilibiliApi(
    private val client: HttpClient,
    private val cookieJarDataSource: CookieJarDataSource,
    private val preferencesDataSource: AsPreferencesDataSource,
) {
    private val logger: Logger = logger<BilibiliApi>()

    val FNVAL_FLAGS = listOf(
//        1,    // MP4 格式，仅 H.264 编码（与 FLV、DASH 格式互斥）
        16,     // DASH 格式	，与 MP4、FLV 格式互斥
        64,     // 是否需求 HDR 视频，需求 DASH 格式，仅 H.265 编码，需要qn=125，大会员认证
        128,    // 是否需求 4K 分辨率，该值与fourk字段协同作用，需要qn=120，大会员认证
        256,    // 是否需求杜比音频，是否需求杜比音频，需求 DASH 格式，大会员认证
        512,    // 是否需求杜比视界，需求 DASH 格式，大会员认证
        1024,   // 是否需求 8K 分辨率，需求 DASH 格式，需要qn=127，大会员认证
        2048    // 是否需求 AV1 编码，需求 DASH 格式
    ).reduce { acc, flag -> acc or flag }

    suspend fun getVideoInfoDetail(bvid: String): BiliVideoData? {
        return client.get("/x/web-interface/view") {
            parameter("bvid", bvid)
        }.body()
    }

    suspend fun getNavigationData(): BilibiliNavigationData {
        return client.get("/x/web-interface/nav").body<BilibiliNavigationData>()
    }

    suspend fun getRedirectUrl(url: String): String {
        return client.get(url).request.url.toString()
    }

    suspend fun getUgcPlayUrl(bvid: String, cid: Long): UgcPlayUrl {
        val signedQuery = buildAndSignPlayUrlParams(bvid, cid)
        return client.get("/x/player/wbi/playurl") {
            url {
                encodedParameters.appendAll(parseQueryString(signedQuery))
            }
        }.body<UgcPlayUrl>()
    }

    suspend fun getPgcPlayUrl(bvid: String, cid: Long): PgcPlayUrl {
        val signedQuery = buildAndSignPlayUrlParams(bvid, cid)
        return client.get("/pgc/player/web/v2/playurl") {
            url {
                encodedParameters.appendAll(parseQueryString(signedQuery))
            }
        }.body()
    }

    private suspend fun buildAndSignPlayUrlParams(bvid: String, cid: Long): String {
        val preferences = preferencesDataSource.userData.first()
        val queryParams = buildMap {
            put("fnver", 0)
            put("fnval", FNVAL_FLAGS)
            put("fourk", 1)
            put("bvid", bvid)
            put("cid", cid)
            put("voice_balance", 1)
            put("gaia_source", "pre-load")
            put("isGaiaAvoided", true)
            put("web_location", 1315873)

            if (preferences.enableTryLook) {
                put("try_look", 1)
            }
        }
        return WbiSign.enc(queryParams)
    }

    suspend fun getSeasonDetailsBySeasonId(ss: String): Season? {
        return client.get("pgc/view/web/season") {
            parameter("season_id", ss)
        }.body()
    }

    suspend fun getSeasonDetailsByEpisodeId(ep: String): Season? {
        return client.get("pgc/view/web/season") {
            parameter("ep_id", ep)
        }.body()
    }

    suspend fun dmSegMobile(aid: Long, cid: Long, duration: Int): List<DmSegMobileReply> {
        val repetitionCount = (duration / 3600) + 1
        return List(repetitionCount) {
            val map = buildMap {
                put("type", 1)
                put("oid", cid)
                put("segment_index", it + 1)
                put("pid", aid)
            }
            val signedQuery = WbiSign.enc(map)
            client.get("x/v2/dm/wbi/web/seg.so") {
                url {
                    encodedParameters.appendAll(parseQueryString(signedQuery))
                }
            }.body<DmSegMobileReply>()
        }
    }

    suspend fun getUserProfile(cookieText: String? = null): UserProfile {
        return client.get("x/member/web/account") {
            cookieText?.let {
                parseClientCookiesHeader(it)
                    .forEach {
                        headers[HttpHeaders.Cookie] =
                            headers[HttpHeaders.Cookie] + "; " + renderSetCookieHeader(
                                it.key,
                                decodeCookieValue(it.value, CookieEncoding.URI_ENCODING),
                                CookieEncoding.RAW
                            )
                    }
            }
        }.body<UserProfile>()
    }

    suspend fun setCookieFromSetCookieHeader(cookieText: String) {
        if (cookieText.isBlank()) {
            logger.debug { "setCookie called with blank cookieText. Nothing to parse." }
            return
        }
        parseClientCookiesHeader(cookieText)
            .forEach { (name, encodedValue) ->
                cookieJarDataSource.add(
                    name,
                    decodeCookieValue(encodedValue, CookieEncoding.URI_ENCODING),
                )
            }
    }

    suspend fun getPlayerInfo(aid: Long, cid: Long): PlayerInfo {
        val map = buildMap {
            put("aid", aid)
            put("cid", cid)
        }
        val signedQuery = WbiSign.enc(map)
        return client.get("x/player/wbi/v2") {
            url {
                encodedParameters.appendAll(parseQueryString(signedQuery))
            }
        }.body()
    }

    /**
     * 获取互动视频特定选择分支的详细信息。
     *
     * @param aid 视频的稿件ID。
     * @param interactionGraphVersion 互动图的版本号。
     * @param choiceId 用户做出的选择或要跳转到的特定分支的ID (原edgeId)。如果为null，则获取初始信息。
     * @return 成功时返回 [InteractiveChoiceDetails]，失败时返回 null。
     */
    suspend fun getInteractiveChoiceOutcome(
        aid: Long,
        bvid: String,
        interactionGraphVersion: Int,
        choiceId: Long?
    ): InteractiveChoiceDetails? {
        return client.get("x/stein/edgeinfo_v2") {
            parameter("aid", aid)
            parameter("bvid", bvid)
            parameter("graph_version", interactionGraphVersion)
            parameter("edge_id", choiceId)
        }.body()
    }
}
