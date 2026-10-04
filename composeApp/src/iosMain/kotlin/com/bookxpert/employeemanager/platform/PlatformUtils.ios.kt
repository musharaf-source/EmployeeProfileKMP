package com.bookxpert.employeemanager.platform

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle

actual object PlatformUtils {

    actual fun formatCurrency(amount: Double): String {
        val formatter = NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterCurrencyStyle
        }
        return formatter.stringFromNumber(NSNumber(double = amount)) ?: "₹$amount"
    }

    actual fun formatDate(epochMillis: Long): String {
        if (epochMillis <= 0) return "-"
        val date = NSDate(timeIntervalSince1970 = epochMillis / 1000.0)
        val formatter = NSDateFormatter().apply {
            dateFormat = "dd MMM yyyy"
        }
        return formatter.stringFromDate(date)
    }

    actual fun formatDateTime(epochMillis: Long): String {
        if (epochMillis <= 0) return "-"
        val date = NSDate(timeIntervalSince1970 = epochMillis / 1000.0)
        val formatter = NSDateFormatter().apply {
            dateFormat = "dd MMM yyyy, hh:mm a"
        }
        return formatter.stringFromDate(date)
    }

    actual fun openFile(localUri: String, mimeType: String) {
        // QuickLook or DocumentInteraction handled via iOS view controller
    }
}
