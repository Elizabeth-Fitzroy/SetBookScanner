package com.example.setbookscanner.util

import java.net.URLEncoder

object MarketUrlGenerator {
    /**
     * メルカリでの全巻セット検索URLを生成
     */
    fun getMercariSearchUrl(title: String): String {
        val query = "$title 全巻セット"
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        return "https://jp.mercari.com/search?keyword=$encodedQuery"
    }

    /**
     * Amazonでの全巻セット検索URLを生成
     */
    fun getAmazonSearchUrl(title: String): String {
        val query = "$title 全巻セット"
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        return "https://www.amazon.co.jp/s?k=$encodedQuery"
    }
}
