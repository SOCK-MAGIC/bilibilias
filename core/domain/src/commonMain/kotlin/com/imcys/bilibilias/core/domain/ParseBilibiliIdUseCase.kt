package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.domain.model.PgcId
import com.imcys.bilibilias.core.domain.model.UgcId

import io.github.oshai.kotlinlogging.KotlinLogging

class ParseBilibiliIdUseCase {
    private data class PatternDefinition(
        val regex: Regex,
        val factory: (MatchResult.Destructured) -> MatchResult
    )

    private val patterns = listOf(
        PatternDefinition(CHEESE_REGEX_PATTERN) { (typeStr, id) ->
            val id = when (typeStr.lowercase()) {
                "ss" -> PgcId.Ss(id)
                "ep" -> PgcId.Ep(id)
                else -> throw IllegalStateException("Unknown cheese type")
            }
            MatchResult.Cheese(id)
        },

        PatternDefinition(AVID_REGEX_PATTERN) { (id) -> MatchResult.UgcMatch(UgcId.Aid(id)) },
        PatternDefinition(BVID_REGEX_PATTERN) { (id) -> MatchResult.UgcMatch(UgcId.Bvid(id)) },

        PatternDefinition(EPID_REGEX_PATTERN) { (id) -> MatchResult.PgcMatch(PgcId.Ep(id)) },
        PatternDefinition(SSID_REGEX_PATTERN) { (id) -> MatchResult.PgcMatch(PgcId.Ss(id)) },
    )

    operator fun invoke(sourceText: String): MatchResult {
        return findMatch(sourceText)
    }

    private fun findMatch(sourceText: String): MatchResult {
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
        data class UgcMatch(val id: UgcId) : MatchResult {
            override fun toString(): String {
                return when (id) {
                    is UgcId.Aid -> id.toString()
                    is UgcId.Bvid -> id.toString()
                }
            }
        }

        data class PgcMatch(val id: PgcId) : MatchResult {
            override fun toString(): String {
                return when (id) {
                    is PgcId.Ep -> id.toString()
                    is PgcId.Ss -> id.toString()
                }
            }
        }

        data class Cheese(val id: PgcId) : MatchResult {
            override fun toString(): String = id.toString()
        }

        data object NoMatch : MatchResult
    }

    companion object {
        private val logger = KotlinLogging.logger{}
        private val BVID_REGEX_PATTERN = Regex("(BV1[1-9A-HJ-NP-Za-km-z]{9})")
        private val AVID_REGEX_PATTERN = Regex("av(\\d+)", RegexOption.IGNORE_CASE)
        private val SSID_REGEX_PATTERN = Regex("ss(\\d+)", RegexOption.IGNORE_CASE)
        private val EPID_REGEX_PATTERN = Regex("ep(\\d+)", RegexOption.IGNORE_CASE)
        private val CHEESE_REGEX_PATTERN = Regex("cheese/play/(ep|ss)(\\d+)")
    }
}
