package com.bookxpert.employeemanager.platform

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

@Composable
actual fun rememberImagePickerLauncher(
    onImagePicked: (localFilePath: String?) -> Unit
): ImagePickerLauncher {
    val context = LocalContext.current
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraFile by remember { mutableStateOf<File?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val copiedPath = copyUriToInternalStorage(context, uri, "profile_imgs", "avatar_${System.currentTimeMillis()}.jpg")
            onImagePicked(copiedPath)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraFile != null && tempCameraFile!!.exists()) {
            onImagePicked(tempCameraFile!!.absolutePath)
        }
    }

    return remember {
        object : ImagePickerLauncher {
            override fun launch(source: ImageSource) {
                when (source) {
                    ImageSource.GALLERY -> galleryLauncher.launch("image/*")
                    ImageSource.CAMERA -> {
                        val imagesDir = File(context.filesDir, "profile_imgs").apply { mkdirs() }
                        val file = File(imagesDir, "cam_${System.currentTimeMillis()}.jpg")
                        tempCameraFile = file
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                        tempCameraUri = uri
                        cameraLauncher.launch(uri)
                    }
                }
            }
        }
    }
}

internal fun copyUriToInternalStorage(context: Context, uri: Uri, subDir: String, fileName: String): String? {
    return try {
        val dir = File(context.filesDir, subDir).apply { mkdirs() }
        val destFile = File(dir, fileName)
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }
        destFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
