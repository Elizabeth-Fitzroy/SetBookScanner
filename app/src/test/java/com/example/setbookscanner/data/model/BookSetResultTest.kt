package com.example.setbookscanner.data.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BookSetResultTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testJsonDeserialization_complete() {
        val jsonString = """
            {
              "title": "鬼滅の刃",
              "detected_volumes": [1, 2, 3, 4, 5],
              "missing_volumes": [],
              "is_complete": true
            }
        """.trimIndent()

        val result = json.decodeFromString<BookSetResult>(jsonString)
        assertEquals("鬼滅の刃", result.title)
        assertEquals(listOf(1, 2, 3, 4, 5), result.detectedVolumes)
        assertTrue(result.missingVolumes.isEmpty())
        assertTrue(result.isComplete)
    }

    @Test
    fun testJsonDeserialization_incomplete() {
        val jsonString = """
            {
              "title": "HUNTER×HUNTER",
              "detected_volumes": [1, 2, 4, 5],
              "missing_volumes": [3],
              "is_complete": false
            }
        """.trimIndent()

        val result = json.decodeFromString<BookSetResult>(jsonString)
        assertEquals("HUNTER×HUNTER", result.title)
        assertEquals(listOf(1, 2, 4, 5), result.detectedVolumes)
        assertEquals(listOf(3), result.missingVolumes)
        assertFalse(result.isComplete)
    }
}
