package com.example.setbookscanner.data.repository

import android.graphics.Bitmap
import com.example.setbookscanner.data.model.BookSetResult
import com.example.setbookscanner.data.remote.GeminiScannerService

class BookScannerRepository(
    private val geminiService: GeminiScannerService = GeminiScannerService()
) {
    suspend fun analyzeSpines(bitmap: Bitmap): Result<BookSetResult> {
        // タイムアウトを防ぎ、高速化するため最大512pxに軽量リサイズ
        val scaledBitmap = scaleBitmapIfNeeded(bitmap, maxDimension = 512)
        return geminiService.analyzeBookSpines(scaledBitmap)
    }

    private fun scaleBitmapIfNeeded(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) return bitmap

        val ratio = width.toFloat() / height.toFloat()
        val targetWidth: Int
        val targetHeight: Int
        if (width > height) {
            targetWidth = maxDimension
            targetHeight = (maxDimension / ratio).toInt()
        } else {
            targetHeight = maxDimension
            targetWidth = (maxDimension * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }
}
