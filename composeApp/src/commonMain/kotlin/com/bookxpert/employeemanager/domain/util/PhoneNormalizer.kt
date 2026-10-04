package com.bookxpert.employeemanager.domain.util

object PhoneNormalizer {

    /**
     * Strips +, 91, 0, spaces, dashes, and parentheses to standardize phone lookup.
     * Time complexity: O(k) where k is string length
     */
    fun normalize(raw: String): String {
        if (raw.isBlank()) return ""
        return raw.replace(Regex("[\\s()\\-+.]"), "")
            .removePrefix("91")
            .removePrefix("0")
    }

    fun isValid10DigitPhone(raw: String): Boolean {
        val normalized = normalize(raw)
        return normalized.length == 10 && normalized.all { it.isDigit() }
    }
}
