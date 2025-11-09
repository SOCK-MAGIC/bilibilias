package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.logging.logger

class ParseBilibiliIdUseCase {
    private fun interface IdMatcher {
        fun match(text: String): MatchResult?
    }

    private val matchers = listOf(
        // av
        IdMatcher { text ->
            AVID_REGEX_PATTERN.find(text)?.let {
                MatchResult.Av(it.groupValues[1])
            }
        },
        // bv
        IdMatcher { text ->
            BVID_REGEX_PATTERN.find(text)?.let {
                MatchResult.Bv(it.value)
            }
        },
        // 课程: cheese123456 或 https://www.bilibili.com/cheese/play/ep123456
        IdMatcher { text ->
            CHEESE_REGEX.find(text)?.let {
                MatchResult.Cheese(it.groupValues[1])
            }
        },
        // ep
        IdMatcher { text ->
            EPID_REGEX_PATTERN.find(text)?.let {
                MatchResult.Ep(it.groupValues[1])
            }
        },
        // ss
        IdMatcher { text ->
            SSID_REGEX_PATTERN.find(text)?.let {
                MatchResult.Ss(it.groupValues[1])
            }
        },
        // 最后匹配短链接
        IdMatcher { text ->
            SHORT_LINK_REGEX_PATTERN.find(text)?.let {
                MatchResult.ShortLink(it.value)
            }
        }
    )

    operator fun invoke(sourceText: String): MatchResult {
        val result = matchers
            .asSequence()
            .mapNotNull { it.match(sourceText) }
            .firstOrNull() ?: MatchResult.NoMatch
        when (result) {
            is MatchResult.NoMatch -> {
                logger.info { "Failed to match any pattern for input: '$sourceText'" }
            }

            else -> {
                logger.debug { "Parsed input '$sourceText' as ${result::class.simpleName} with value '${result.getIdentifier()}'" }
            }
        }
        return result
    }

    sealed interface MatchResult {
        data class Bv(val id: String) : MatchResult
        data class Ep(val id: String) : MatchResult
        data class Ss(val id: String) : MatchResult
        data class Av(val id: String) : MatchResult
        data class ShortLink(val url: String) : MatchResult
        data class Cheese(val id: String) : MatchResult
        data object NoMatch : MatchResult

        fun getIdentifier(): String = when (this) {
            is Av -> id
            is Bv -> id
            is Cheese -> id
            is Ep -> id
            is Ss -> id
            is ShortLink -> url
            is NoMatch -> "N/A"
        }
    }

    companion object {
        private val logger = logger<ParseBilibiliIdUseCase>()
        private val BVID_REGEX_PATTERN = Regex("BV1[1-9A-HJ-NP-Za-km-z]{9}")
        private val AVID_REGEX_PATTERN = Regex("av(\\d+)", RegexOption.IGNORE_CASE)

        private val SSID_REGEX_PATTERN = Regex("ss(\\d+)")
        private val EPID_REGEX_PATTERN = Regex("ep(\\d+)")
        private val SHORT_LINK_REGEX_PATTERN = Regex("^https?://b23\\.tv/[a-zA-Z0-9]+/?$")
        private val CHEESE_REGEX = Regex("(?:cheese/play/ep|cheese)(\\d+)")
    }
}