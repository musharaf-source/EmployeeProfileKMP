package com.bookxpert.employeemanager.util

import com.bookxpert.employeemanager.domain.util.FormValidator
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FormValidatorTest {

    @Test
    fun validateFullName_enforcesMinimumLength() {
        assertNotNull(FormValidator.validateFullName(""))
        assertNotNull(FormValidator.validateFullName("  ab  "))
        assertNull(FormValidator.validateFullName("John Doe"))
    }

    @Test
    fun validateEmail_enforcesCorrectFormat() {
        assertNotNull(FormValidator.validateEmail(""))
        assertNotNull(FormValidator.validateEmail("invalid-email"))
        assertNotNull(FormValidator.validateEmail("user@domain"))
        assertNull(FormValidator.validateEmail("valid.user@bookxpert.com"))
    }

    @Test
    fun validatePhone_enforces10Digits() {
        assertNotNull(FormValidator.validatePhone(""))
        assertNotNull(FormValidator.validatePhone("12345"))
        assertNull(FormValidator.validatePhone("+91 98765 43210"))
        assertNull(FormValidator.validatePhone("9876543210"))
    }

    @Test
    fun validateAddress_enforcesMin6Chars() {
        assertNotNull(FormValidator.validateAddress(""))
        assertNotNull(FormValidator.validateAddress("Main"))
        assertNull(FormValidator.validateAddress("123 Main Street"))
    }

    @Test
    fun validateSkills_requiresAtLeastOneSkill() {
        assertNotNull(FormValidator.validateSkills(emptyList()))
        assertNull(FormValidator.validateSkills(listOf("Kotlin")))
    }

    @Test
    fun validateJoiningDate_disallowsFutureDates() {
        val now = 1700000000000L
        val future = now + 1000000000L
        val past = now - 1000000000L

        assertNotNull(FormValidator.validateJoiningDate(future, now))
        assertNull(FormValidator.validateJoiningDate(past, now))
    }

    @Test
    fun validateSalary_requiresPositiveNumber() {
        assertNotNull(FormValidator.validateSalary(""))
        assertNotNull(FormValidator.validateSalary("-500"))
        assertNotNull(FormValidator.validateSalary("abc"))
        assertNull(FormValidator.validateSalary("750000"))
        assertNull(FormValidator.validateSalary("750,000.50"))
    }
}
