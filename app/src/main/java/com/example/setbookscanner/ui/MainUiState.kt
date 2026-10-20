package com.example.setbookscanner.ui

import android.graphics.Bitmap
import com.example.setbookscanner.data.model.BookSetResult

sealed interface MainUiState {
    object CameraReady : MainUiState
    data class Analyzing(val capturedImage: Bitmap) : MainUiState
    data class Success(val capturedImage: Bitmap, val result: BookSetResult) : MainUiState
    data class Error(val capturedImage: Bitmap?, val message: String) : MainUiState
}
