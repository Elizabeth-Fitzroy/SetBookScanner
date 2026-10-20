package com.example.setbookscanner.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookSetResult(
    @SerialName("title")
    val title: String,
    @SerialName("detected_volumes")
    val detectedVolumes: List<Int> = emptyList(),
    @SerialName("missing_volumes")
    val missingVolumes: List<Int> = emptyList(),
    @SerialName("is_complete")
    val isComplete: Boolean = false
)
