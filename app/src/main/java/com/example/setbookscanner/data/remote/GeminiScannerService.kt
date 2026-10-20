package com.example.setbookscanner.data.remote

import android.graphics.Bitmap
import com.example.setbookscanner.BuildConfig
import com.example.setbookscanner.data.model.BookSetResult
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class GeminiScannerService(private val apiKey: String = BuildConfig.GEMINI_API_KEY) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun analyzeBookSpines(bitmap: Bitmap): Result<BookSetResult> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalArgumentException("Gemini API Keyが設定されていません。local.properties に GEMINI_API_KEY を設定してください。")
            )
        }

        try {
            val systemInstructionText = """
                あなたは古本（漫画・小説・書籍等）の背表紙画像から情報を正確に読み取るAIアシスタントです。
                送信された背表紙の画像から、以下の規則に従って結果をJSON形式で返答してください。
                
                1. title: 識別された作品名（タイトル）。
                2. detected_volumes: 画像に含まれる検出された巻数の昇順整数リスト（例: [1, 2, 3, 4, 5, 7, 8, 9, 10]）。
                3. missing_volumes: 検出された最小巻から最大巻までの範囲で、画像内で抜け落ちている欠品巻数の昇順リスト（例: [6]）。
                4. is_complete: 欠品巻数がなく、全巻（または検出範囲内で連続）揃っている場合は true、抜け巻がある場合は false。
                
                返答は余計な解説を含めず、純粋なJSONオブジェクトのみを出力してください。
            """.trimIndent()

            val config = generationConfig {
                responseMimeType = "application/json"
                temperature = 0.1f
            }

            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey,
                generationConfig = config,
                systemInstruction = content { text(systemInstructionText) }
            )

            val inputContent = content {
                image(bitmap)
                text("この背表紙画像に含まれる本のタイトル、巻数一覧、欠品巻数、コンプリート状態を抽出してJSONで出力してください。")
            }

            val response = generativeModel.generateContent(inputContent)
            val responseText = response.text ?: return@withContext Result.failure(
                IllegalStateException("Gemini APIからのレスポンスが空です。")
            )

            val cleanedJson = responseText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val result = json.decodeFromString<BookSetResult>(cleanedJson)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
