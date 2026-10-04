package com.bookxpert.employeemanager.util

import com.bookxpert.employeemanager.domain.util.PhoneNormalizer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PhoneNormalizerTest {

    @Test
    fun normalize_stripsCountryPrefix91() {
        val input = "+91 98765 43210"
        val expected = "9876543210"
        assertEquals(expected, PhoneNormalizer.normalize(input))
    }

    @Test
    fun normalize_stripsLeadingZero() {
        val input = "09876543210"
        val expected = "9876543210"
        assertEquals(expected, PhoneNormalizer.normalize(input))
    }

    @Test
    fun normalize_stripsDashesSpacesAndParentheses() {
        val input = "(987) 654-3210"
        val expected = "9876543210"
        assertEquals(expected, PhoneNormalizer.normalize(input))
    }

    @Test
    fun isValid10DigitPhone_returnsTrueForValidNumbers() {
        assertTrue(PhoneNormalizer.isValid10DigitPhone("+91 9876543210"))
        assertTrue(PhoneNormalizer.isValid10DigitPhone("9876543210"))
        assertTrue(PhoneNormalizer.isValid10DigitPhone("09876543210"))
    }

    @Test
    fun isValid10DigitPhone_returnsFalseForInvalidLengths() {
        assertFalse(PhoneNormalizer.isValid10DigitPhone("12345"))
        assertFalse(PhoneNormalizer.isValid10DigitPhone("9876543210123"))
        assertFalse(PhoneNormalizer.isValid10DigitPhone(""))
    }
}
