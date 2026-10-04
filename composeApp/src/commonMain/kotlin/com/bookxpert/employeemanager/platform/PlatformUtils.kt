package com.bookxpert.employeemanager.platform

expect object PlatformUtils {
    fun formatCurrency(amount: Double): String
    fun formatDate(epochMillis: Long): String
    fun formatDateTime(epochMillis: Long): String
    fun openFile(localUri: String, mimeType: String)
}
