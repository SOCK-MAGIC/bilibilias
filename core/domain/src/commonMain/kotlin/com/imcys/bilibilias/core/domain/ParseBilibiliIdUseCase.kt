package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.domain.model.CheeseType
import com.imcys.bilibilias.core.logging.logger

class ParseBilibiliIdUseCase {
    private data class PatternDefinition(
        val regex: Regex,
        // 使用一个接收String并返回Success结果的构造函数引用
        val factory: (MatchResult.Destructured) -> MatchResult
    )

    private val patterns = listOf(
        PatternDefinition(CHEESE_REGEX_PATTERN) { (typeStr, id) ->
            val type = when (typeStr.lowercase()) {
                "ss" -> CheeseType.SEASON
                "ep" -> CheeseType.EPISODE
                else -> throw IllegalStateException("Unknown cheese type")
            }
            MatchResult.Cheese(id, type)
        },

        PatternDefinition(AVID_REGEX_PATTERN) { (id) -> MatchResult.Av(id) },
        PatternDefinition(BVID_REGEX_PATTERN) { (id) -> MatchResult.Bv(id) },

        PatternDefinition(EPID_REGEX_PATTERN) { (id) -> MatchResult.Ep(id) },
        PatternDefinition(SSID_REGEX_PATTERN) { (id) -> MatchResult.Ss(id) },
        PatternDefinition(SHORT_LINK_REGEX_PATTERN) { (code) -> MatchResult.ShortLink(code) }
    )

    operator fun invoke(sourceText: String): MatchResult {
        val result = patterns
            .asSequence()
            .mapNotNull { pattern ->
                pattern.regex.find(sourceText)?.let { match ->
                    pattern.factory(match.destructured)
                }
            }
            .firstOrNull() ?: MatchResult.NoMatch

        when (result) {
            is MatchResult.NoMatch -> {
                logger.info { "未匹配到任何Bilibili ID: '$sourceText'" }
            }

            is MatchResult -> {
                logger.debug { "解析 '$sourceText' 成功，类型: ${result::class.simpleName}, ID: '${result}'" }
            }
        }
        return result
    }

    sealed interface MatchResult {
        data class Bv(val id: String) : MatchResult {
            override fun toString(): String = id
        }

        data class Ep(val id: String) : MatchResult {
            override fun toString(): String = id
        }

        data class Ss(val id: String) : MatchResult {
            override fun toString(): String = id
        }

        data class Av(val id: String) : MatchResult {
            override fun toString(): String = id
        }

        data class ShortLink(val url: String) : MatchResult {
            override fun toString(): String = url
        }

        data class Cheese(val id: String, val type: CheeseType) : MatchResult {
            override fun toString(): String = "${type.name.lowercase()}$id"
        }

        data object NoMatch : MatchResult
    }

    companion object {
        private val logger = logger<ParseBilibiliIdUseCase>()
        private val BVID_REGEX_PATTERN =
            Regex("BV1[1-9A-HJ-NP-Za-km-z]{9}", RegexOption.IGNORE_CASE)
        private val AVID_REGEX_PATTERN = Regex("av(\\d+)", RegexOption.IGNORE_CASE)

        private val SSID_REGEX_PATTERN = Regex("ss(\\d+)", RegexOption.IGNORE_CASE)
        private val EPID_REGEX_PATTERN = Regex("ep(\\d+)", RegexOption.IGNORE_CASE)

        private val SHORT_LINK_REGEX_PATTERN = Regex("^https?://b23\\.tv/[a-zA-Z0-9]+/?$")

        private val CHEESE_REGEX_PATTERN = Regex("cheese/play/(ep|ss)(\\d+)")
    }
}
