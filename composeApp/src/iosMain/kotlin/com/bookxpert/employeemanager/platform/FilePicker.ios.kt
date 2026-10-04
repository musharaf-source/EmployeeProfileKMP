package com.bookxpert.employeemanager.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberDocumentPickerLauncher(
    onResult: (DocumentPickerResult) -> Unit
): DocumentPickerLauncher {
    return remember {
        object : DocumentPickerLauncher {
            override fun launch() {
                onResult(DocumentPickerResult.Cancelled)
            }
        }
    }
}
