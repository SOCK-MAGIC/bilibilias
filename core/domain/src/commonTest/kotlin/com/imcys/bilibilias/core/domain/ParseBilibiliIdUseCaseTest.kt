package com.imcys.bilibilias.core.domain


import com.imcys.bilibilias.core.domain.model.PgcId
import com.imcys.bilibilias.core.domain.model.UgcId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ParseBilibiliIdUseCaseTest {

    private val useCase = ParseBilibiliIdUseCase()

    @Test
    fun `当输入为有效BVID时，应返回UgcMatch Bvid`() {
        val input = "BV1LY411s75s"
        val result = useCase(input)

        // 断言结果是 UgcMatch 类型
        assertIs<ParseBilibiliIdUseCase.MatchResult.UgcMatch>(result)
        // 断言 ID 是 Bvid 类型
        val ugcId = result.id
        assertIs<UgcId.Bvid>(ugcId)
        // 断言 ID 的值是正确的
        assertEquals(input, ugcId.id)
    }

    @Test
    fun `当输入为有效avid时，应返回UgcMatch Aid`() {
        val input = "av170001"
        val result = useCase(input)

        assertIs<ParseBilibiliIdUseCase.MatchResult.UgcMatch>(result)
        val ugcId = result.id
        assertIs<UgcId.Aid>(ugcId)
        assertEquals("170001", ugcId.id)
    }

    @Test
    fun `当输入为大写的AVid时，应返回UgcMatch Aid`() {
        val input = "AV170001"
        val result = useCase(input)

        assertIs<ParseBilibiliIdUseCase.MatchResult.UgcMatch>(result)
        val ugcId = result.id
        assertIs<UgcId.Aid>(ugcId)
        assertEquals("170001", ugcId.id)
    }

    @Test
    fun `当输入为有效epid时，应返回PgcMatch Ep`() {
        val input = "ep322216"
        val result = useCase(input)

        assertIs<ParseBilibiliIdUseCase.MatchResult.PgcMatch>(result)
        val pgcId = result.id
        assertIs<PgcId.Ep>(pgcId)
        assertEquals("322216", pgcId.id)
    }

    @Test
    fun `当输入为有效ssid时，应返回PgcMatch Ss`() {
        val input = "ss28233"
        val result = useCase(input)

        assertIs<ParseBilibiliIdUseCase.MatchResult.PgcMatch>(result)
        val pgcId = result.id
        assertIs<PgcId.Ss>(pgcId)
        assertEquals("28233", pgcId.id)
    }

    @Test
    fun `当输入为cheese ep链接时，应返回Cheese Ep`() {
        val input = "https://www.bilibili.com/cheese/play/ep8888"
        val result = useCase(input)

        assertIs<ParseBilibiliIdUseCase.MatchResult.Cheese>(result)
        val pgcId = result.id
        assertIs<PgcId.Ep>(pgcId)
        assertEquals("8888", pgcId.id)
    }

    @Test
    fun `当输入为cheese ss链接时，应返回Cheese Ss`() {
        val input = "https://www.bilibili.com/cheese/play/ss9999"
        val result = useCase(input)

        assertIs<ParseBilibiliIdUseCase.MatchResult.Cheese>(result)
        val pgcId = result.id
        assertIs<PgcId.Ss>(pgcId)
        assertEquals("9999", pgcId.id)
    }

    @Test
    fun `当输入为无效字符串时，应返回NoMatch`() {
        val input = "这是一段不包含任何ID的普通文本"
        val result = useCase(input)
        assertIs<ParseBilibiliIdUseCase.MatchResult.NoMatch>(result)
    }

    @Test
    fun `当ID嵌入在URL中时，应能正确提取`() {
        val input =
            "https://www.bilibili.com/video/BV1fK4y1L7bS/?spm_id_from=333.1007.tianma.1-1-1.click"
        val result = useCase(input)

        assertIs<ParseBilibiliIdUseCase.MatchResult.UgcMatch>(result)
        val ugcId = result.id
        assertIs<UgcId.Bvid>(ugcId)
        assertEquals("BV1fK4y1L7bS", ugcId.id)
    }

    @Test
    fun `当字符串包含多个ID时，应返回按规则顺序的第一个匹配项`() {
        val input = "这是一个BVID: BV1fK4y1L7bS，但还有一个avid: av98765"
        val result = useCase(input)

        // 应该匹配到 avid
        assertIs<ParseBilibiliIdUseCase.MatchResult.UgcMatch>(result)
        val ugcId = result.id
        assertIs<UgcId.Aid>(ugcId)
        assertEquals("98765", ugcId.id)
    }

    @Test
    fun `当字符串包含cheese和avid时，应返回cheese的匹配项`() {
        val input =
            "这是一个av12345，但还有一个cheese链接: https://www.bilibili.com/cheese/play/ss54321"
        val result = useCase(input)

        // 应该匹配到 cheese
        assertIs<ParseBilibiliIdUseCase.MatchResult.Cheese>(result)
        val pgcId = result.id
        assertIs<PgcId.Ss>(pgcId)
        assertEquals("54321", pgcId.id)
    }
}