package com.example.setbookscanner.util

import org.junit.Assert.assertTrue
import org.junit.Test

class MarketUrlGeneratorTest {

    @Test
    fun testMercariUrlGeneration() {
        val title = "ONE PIECE"
        val actual = MarketUrlGenerator.getMercariSearchUrl(title)
        assertTrue(actual.startsWith("https://jp.mercari.com/search?keyword="))
        assertTrue(actual.contains("ONE"))
    }

    @Test
    fun testAmazonUrlGeneration() {
        val title = "進撃の巨人"
        val actual = MarketUrlGenerator.getAmazonSearchUrl(title)
        assertTrue(actual.startsWith("https://www.amazon.co.jp/s?k="))
    }
}
