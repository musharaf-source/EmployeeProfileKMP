package com.bookxpert.employeemanager.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberImagePickerLauncher(
    onImagePicked: (localFilePath: String?) -> Unit
): ImagePickerLauncher {
    return remember {
        object : ImagePickerLauncher {
            override fun launch(source: ImageSource) {
                // Invoked natively or stubbed for preview
                onImagePicked(null)
            }
        }
    }
}
