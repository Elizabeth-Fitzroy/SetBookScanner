package com.example.setbookscanner.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.setbookscanner.data.model.BookSetResult
import com.example.setbookscanner.data.repository.BookScannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: BookScannerRepository = BookScannerRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.CameraReady)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun processCapturedImage(bitmap: Bitmap) {
        _uiState.value = MainUiState.Analyzing(bitmap)

        viewModelScope.launch {
            val result = repository.analyzeSpines(bitmap)
            result.onSuccess { bookSetResult ->
                _uiState.value = MainUiState.Success(bitmap, bookSetResult)
            }.onFailure { exception ->
                _uiState.value = MainUiState.Error(
                    capturedImage = bitmap,
                    message = exception.localizedMessage ?: "解析中にエラーが発生しました。"
                )
            }
        }
    }

    fun resetToCamera() {
        _uiState.value = MainUiState.CameraReady
    }
}
