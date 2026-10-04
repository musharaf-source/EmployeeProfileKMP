package com.bookxpert.employeemanager.platform

import android.content.Intent
import androidx.core.content.FileProvider
import com.bookxpert.employeemanager.data.local.database.appContext
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual object PlatformUtils {

    actual fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        return format.format(amount)
    }

    actual fun formatDate(epochMillis: Long): String {
        if (epochMillis <= 0) return "-"
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(epochMillis))
    }

    actual fun formatDateTime(epochMillis: Long): String {
        if (epochMillis <= 0) return "-"
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(epochMillis))
    }

    actual fun openFile(localUri: String, mimeType: String) {
        try {
            val file = File(localUri)
            if (!file.exists()) return
            val uri = FileProvider.getUriForFile(
                appContext,
                "${appContext.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            appContext.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
