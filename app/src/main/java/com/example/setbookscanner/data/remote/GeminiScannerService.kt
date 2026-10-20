package com.example.setbookscanner.data.remote

import android.graphics.Bitmap
import android.util.Log
import com.example.setbookscanner.BuildConfig
import com.example.setbookscanner.data.model.BookSetResult
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class GeminiScannerService(private val apiKey: String = BuildConfig.GEMINI_API_KEY) {

    companion object {
        private const val TAG = "GeminiScannerService"
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun analyzeBookSpines(bitmap: Bitmap): Result<BookSetResult> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            Log.e(TAG, "API Key is blank!")
            return@withContext Result.failure(
                IllegalArgumentException("Gemini API Keyが設定されていません。local.propertiesに有効な GEMINI_API_KEY を設定してください。")
            )
        }

        // 無料で利用可能な最新モデル（メイン: 3.8-flash、フォールバック: 2.5-flash）
        val modelsToTry = listOf("gemini-3.8-flash", "gemini-2.5-flash")
        var lastException: Exception? = null

        for (modelName in modelsToTry) {
            try {
                Log.d(TAG, "Trying Gemini model: $modelName")
                val generativeModel = GenerativeModel(
                    modelName = modelName,
                    apiKey = apiKey
                )

                val prompt = """
                    あなたは古本の背表紙画像から情報を抽出するAIです。
                    画像から作品名、検出された巻数の一覧、欠品巻数、全巻揃っているかの判定結果を以下の厳密なJSON形式のみで返答してください。余分な挨拶や解説、マークダウン（```json等）は一切出力しないでください。
                    {
                      "title": "作品名",
                      "detected_volumes": [1, 2, 3],
                      "missing_volumes": [],
                      "is_complete": false
                    }
                """.trimIndent()

                val inputContent = content {
                    image(bitmap)
                    text(prompt)
                }

                val response = generativeModel.generateContent(inputContent)
                val responseText = response.text ?: continue

                val cleanedJson = responseText.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val result = json.decodeFromString<BookSetResult>(cleanedJson)
                Log.d(TAG, "Successfully analyzed with model $modelName: $result")
                return@withContext Result.success(result)
            } catch (e: Exception) {
                Log.e(TAG, "Failed with model $modelName: ${e.message}", e)
                lastException = e
            }
        }

        val errorMessage = lastException?.message ?: "不明なエラー"
        return@withContext Result.failure(
            RuntimeException("Gemini APIエラー: $errorMessage (Google AI StudioのAPIキーとネットワークをご確認ください)")
        )
    }
}
