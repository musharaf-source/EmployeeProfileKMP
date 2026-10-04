package com.bookxpert.employeemanager.platform

import androidx.compose.runtime.Composable
import com.bookxpert.employeemanager.domain.model.DocumentMetadata

interface DocumentPickerLauncher {
    fun launch()
}

sealed interface DocumentPickerResult {
    data class Success(val metadata: DocumentMetadata) : DocumentPickerResult
    data class Error(val message: String) : DocumentPickerResult
    data object Cancelled : DocumentPickerResult
}

@Composable
expect fun rememberDocumentPickerLauncher(
    onResult: (DocumentPickerResult) -> Unit
): DocumentPickerLauncher
