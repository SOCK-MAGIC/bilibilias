package com.imcys.bilibilias.core.domain

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ParseBilibiliIdUseCaseTest {

    private lateinit var useCase: ParseBilibiliIdUseCase

    @BeforeTest
    fun setUp() {
        useCase = ParseBilibiliIdUseCase()
    }

    @Test
    fun `given simple av id, then returns Av result`() {
        val input = "av170001"
        val result = useCase(input)

        assertTrue { result is ParseBilibiliIdUseCase.MatchResult.Av }
        assertEquals("170001", (result as ParseBilibiliIdUseCase.MatchResult.Av).id)
    }

    @Test
    fun `given uppercase AV id, then returns Av result`() {
        val input = "AV170001"
        val result = useCase(input)

        assertTrue { result is ParseBilibiliIdUseCase.MatchResult.Av }
        assertEquals("170001", (result as ParseBilibiliIdUseCase.MatchResult.Av).id)
    }

    @Test
    fun `given full url with av id, then returns Av result`() {
        val input = "https://www.bilibili.com/video/av170001/?p=2&spm_id_from=333.88.videocard.6"
        val result = useCase(input)

        assertTrue { result is ParseBilibiliIdUseCase.MatchResult.Av }
        assertEquals("170001", (result as ParseBilibiliIdUseCase.MatchResult.Av).id)
    }

    @Test
    fun `given simple bv id, then returns Bv result`() {
        val input = "BV17x411w7KC"
        val result = useCase(input)

        assertTrue { result is ParseBilibiliIdUseCase.MatchResult.Bv }
        assertEquals("BV17x411w7KC", (result as ParseBilibiliIdUseCase.MatchResult.Bv).id)
    }

    @Test
    fun `given text containing bv id, then returns Bv result`() {
        val input = "分享视频：BV17x411w7KC，快来看看吧！"
        val result = useCase(input)

        assertTrue { result is ParseBilibiliIdUseCase.MatchResult.Bv }
        assertEquals("BV17x411w7KC", (result as ParseBilibiliIdUseCase.MatchResult.Bv).id)
    }

    @Test
    fun `given lowercase bv id, then returns NoMatch`() {
        // 正则表达式要求 "BV" 是大写
        val input = "bv17x411w7kc"
        val result = useCase(input)
        assertTrue { result is ParseBilibiliIdUseCase.MatchResult.NoMatch }
    }

    @Test
    fun `given simple ep id, then returns Ep result`() {
        val input = "ep468413"
        val result = useCase(input)

        assertTrue { result is ParseBilibiliIdUseCase.MatchResult.Ep }
        assertEquals("468413", (result as ParseBilibiliIdUseCase.MatchResult.Ep).id)
    }

    @Test
    fun `given full bangumi url with ep id, then returns Ep result`() {
        val input = "https://www.bilibili.com/bangumi/play/ep468413"
        val result = useCase(input)

        assertTrue { result is ParseBilibiliIdUseCase.MatchResult.Ep }
        assertEquals("468413", (result as ParseBilibiliIdUseCase.MatchResult.Ep).id)
    }


    @Test
    fun `given simple ss id, then returns Ss result`() {
        val input = "ss33980"
        val result = useCase(input)

        assertTrue { result is ParseBilibiliIdUseCase.MatchResult.Ss }
        assertEquals("33980", (result as ParseBilibiliIdUseCase.MatchResult.Ss).id)
    }

    @Test
    fun `given full bangumi url with ss id, then returns Ss result`() {
        val input = "https://www.bilibili.com/bangumi/play/ss33980"
        val result = useCase(input)

        assertTrue { result is ParseBilibiliIdUseCase.MatchResult.Ss }
        assertEquals("33980", (result as ParseBilibiliIdUseCase.MatchResult.Ss).id)
    }

    @Test
    fun `given simple cheese id, then returns Cheese result`() {
        val input = "cheese123456"
        val result = useCase(input)

        assertTrue { result is ParseBilibiliIdUseCase.MatchResult.Cheese }
        assertEquals("123456", (result as ParseBilibiliIdUseCase.MatchResult.Cheese).id)
    }

    @Test
    fun `given full cheese url, then returns Cheese result`() {
        val input = "https://www.bilibili.com/cheese/play/ep654321"
        val result = useCase(input)

        assertTrue { result is ParseBilibiliIdUseCase.MatchResult.Cheese }
        assertEquals("654321", (result as ParseBilibiliIdUseCase.MatchResult.Cheese).id)
    }


    @Test
    fun `given http b23 tv url, then returns ShortLink result`() {
        val input = "http://b23.tv/Dastini"
        val result = useCase(input)

        assertTrue { result is ParseBilibiliIdUseCase.MatchResult.ShortLink }
        assertEquals(input, (result as ParseBilibiliIdUseCase.MatchResult.ShortLink).url)
    }

    @Test
    fun `given https b23 tv url with trailing slash, then returns ShortLink result`() {
        val input = "https://b23.tv/Dastini/"
        val result = useCase(input)

        assertTrue { result is ParseBilibiliIdUseCase.MatchResult.ShortLink }
        assertEquals(input, (result as ParseBilibiliIdUseCase.MatchResult.ShortLink).url)
    }

    @Test
    fun `given text containing a short link, then returns NoMatch`() {
        val input = "Check this: https://b23.tv/Dastini/"
        val result = useCase(input)
        assertTrue { result is ParseBilibiliIdUseCase.MatchResult.NoMatch }
    }

    @Test
    fun `given completely unrelated string, then returns NoMatch`() {
        val input = "Hello, world!"
        val result = useCase(input)
        assertTrue { result is ParseBilibiliIdUseCase.MatchResult.NoMatch }
    }

    @Test
    fun `given a google url, then returns NoMatch`() {
        val input = "https://www.google.com"
        val result = useCase(input)
        assertTrue { result is ParseBilibiliIdUseCase.MatchResult.NoMatch }
    }

    @Test
    fun `given text with multiple potential matches, then returns the first one in priority list`() {
        val input = "My favorite videos are av170001 and BV17x411w7KC"
        val result = useCase(input)

        assertTrue { result is ParseBilibiliIdUseCase.MatchResult.Av }
        assertEquals("170001", (result as ParseBilibiliIdUseCase.MatchResult.Av).id)
    }
}