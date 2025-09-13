package com.imcys.bilibilias.core.domain

import com.imcys.bilibilias.core.logging.logger

class GetIdFromTextUseCase {

    operator fun invoke(text: String): MatchResult {
        AVID_REGEX_PATTERN.find(text)?.let {
            return MatchResult.Av(it.groupValues[1])
        }
        BVID_REGEX_PATTERN.find(text)?.let {
            return MatchResult.Bv(it.value)
        }
        EPID_REGEX_PATTERN.find(text)?.let {
            return MatchResult.Ep(it.groupValues[1])
        }
        SSID_REGEX_PATTERN.find(text)?.let {
            return MatchResult.Ss(it.groupValues[1])
        }

        SHORT_LINK_REGEX_PATTERN.find(text)?.let {
            return MatchResult.Http(it.value)
        }
        return MatchResult.Empty
    }

    sealed interface MatchResult {
        data class Bv(val id: String) : MatchResult
        data class Ep(val id: String) : MatchResult
        data class Ss(val id: String) : MatchResult
        data class Av(val id: String) : MatchResult
        data class Http(val text: String) : MatchResult
        data object Empty : MatchResult
    }

    companion object {
        private val logger = logger<GetIdFromTextUseCase>()
        private val BVID_REGEX_PATTERN = Regex("BV1[1-9A-HJ-NP-Za-km-z]{9}")
        private val AVID_REGEX_PATTERN = Regex("av(\\d+)", RegexOption.IGNORE_CASE)

        private val SSID_REGEX_PATTERN = Regex("ss(\\d+)", RegexOption.IGNORE_CASE)
        private val EPID_REGEX_PATTERN = Regex("ep(\\d+)", RegexOption.IGNORE_CASE)
        private val SHORT_LINK_REGEX_PATTERN = Regex("^https://b23\\.tv(/.*)?$")
    }
}