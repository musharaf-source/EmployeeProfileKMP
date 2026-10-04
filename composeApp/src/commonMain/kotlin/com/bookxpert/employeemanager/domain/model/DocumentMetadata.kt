package com.bookxpert.employeemanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DocumentMetadata(
    val fileName: String,
    val fileSizeBytes: Long,
    val mimeType: String,
    val localUri: String
) {
    val formattedSize: String
        get() {
            val kb = fileSizeBytes / 1024.0
            val mb = kb / 1024.0
            return when {
                mb >= 1.0 -> "${(mb * 10.0).toLong() / 10.0} MB"
                kb >= 1.0 -> "${(kb * 10.0).toLong() / 10.0} KB"
                else -> "$fileSizeBytes B"
            }
        }
}
