package io.github.yudai0308.honma.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class StringUtilTest {
    @Test
    fun divideWithComma_givenListHas3Items_returnsString() {
        val list = listOf("伊藤博文", "黒田清隆", "山縣有朋")
        val actual = StringUtil.divideWithComma(list)
        assertThat(actual).isEqualTo("伊藤博文, 黒田清隆, 山縣有朋")
    }

    @Test
    fun divideWithComma_givenListHas3ItemsEmptyIncluded_returnsString() {
        val list = listOf("伊藤博文", "", "山縣有朋")
        val actual = StringUtil.divideWithComma(list)
        assertThat(actual).isEqualTo("伊藤博文, 山縣有朋")
    }

    @Test
    fun divideWithComma_givenEmptyOnlyList_returnsEmptyString() {
        val list = listOf("", "", "")
        val actual = StringUtil.divideWithComma(list)
        assertThat(actual).isEqualTo("")
    }
}