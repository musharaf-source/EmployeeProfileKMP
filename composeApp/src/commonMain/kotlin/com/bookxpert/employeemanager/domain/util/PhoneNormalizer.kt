package com.bookxpert.employeemanager.domain.util

/**
 * Utility for phone number sanitization and format normalization.
 * Strips formatting symbols (+, spaces, dashes, brackets) and common country prefixes
 * so comparisons and indexing remain deterministic.
 */
object PhoneNormalizer {

    /**
     * Normalizes a raw phone string down to its base digits.
     * Time Complexity: O(k) where k is the length of the raw phone string.
     * Space Complexity: O(k) for the string builder output.
     */
    fun normalize(raw: String): String {
        if (raw.isBlank()) return ""
        val digitsOnly = raw.replace(Regex("[\\s()\\-+.]"), "")
        return digitsOnly
            .removePrefix("91")
            .removePrefix("0")
    }

    /**
     * Checks if the normalized string consists of exactly 10 digits.
     */
    fun isValid10DigitPhone(raw: String): Boolean {
        val normalized = normalize(raw)
        return normalized.length == 10 && normalized.all { it.isDigit() }
    }
}
