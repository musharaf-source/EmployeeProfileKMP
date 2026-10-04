package com.bookxpert.employeemanager.platform

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.bookxpert.employeemanager.domain.model.DocumentMetadata
import java.io.File
import java.io.FileOutputStream

@Composable
actual fun rememberDocumentPickerLauncher(
    onResult: (DocumentPickerResult) -> Unit
): DocumentPickerLauncher {
    val context = LocalContext.current

    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri == null) {
            onResult(DocumentPickerResult.Cancelled)
            return@rememberLauncherForActivityResult
        }

        try {
            var fileName = "resume_${System.currentTimeMillis()}"
            var fileSize = 0L

            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) fileName = cursor.getString(nameIndex) ?: fileName
                    if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
                }
            }

            val maxSizeBytes = 5 * 1024 * 1024 // 5 MB
            if (fileSize > maxSizeBytes) {
                onResult(DocumentPickerResult.Error("File exceeds maximum allowed size of 5 MB"))
                return@rememberLauncherForActivityResult
            }

            val mimeType = context.contentResolver.getType(uri) ?: "application/pdf"
            val targetDir = File(context.filesDir, "documents").apply { mkdirs() }
            val cleanFileName = fileName.replace("[^a-zA-Z0-9._-]".toRegex(), "_")
            val destFile = File(targetDir, "${System.currentTimeMillis()}_$cleanFileName")

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }

            val actualSize = if (fileSize > 0) fileSize else destFile.length()
            if (actualSize > maxSizeBytes) {
                destFile.delete()
                onResult(DocumentPickerResult.Error("File exceeds maximum allowed size of 5 MB"))
                return@rememberLauncherForActivityResult
            }

            val metadata = DocumentMetadata(
                fileName = fileName,
                fileSizeBytes = actualSize,
                mimeType = mimeType,
                localUri = destFile.absolutePath
            )
            onResult(DocumentPickerResult.Success(metadata))
        } catch (e: Exception) {
            onResult(DocumentPickerResult.Error("Failed to import document: ${e.localizedMessage ?: "Unknown error"}"))
        }
    }

    return remember {
        object : DocumentPickerLauncher {
            override fun launch() {
                val mimeTypes = arrayOf(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                )
                documentLauncher.launch(mimeTypes)
            }
        }
    }
}
