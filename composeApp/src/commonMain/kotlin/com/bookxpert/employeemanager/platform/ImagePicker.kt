package com.bookxpert.employeemanager.platform

import androidx.compose.runtime.Composable

enum class ImageSource {
    CAMERA,
    GALLERY
}

interface ImagePickerLauncher {
    fun launch(source: ImageSource)
}

@Composable
expect fun rememberImagePickerLauncher(
    onImagePicked: (localFilePath: String?) -> Unit
): ImagePickerLauncher
