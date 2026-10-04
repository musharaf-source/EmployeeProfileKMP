package com.bookxpert.employeemanager.domain.util

import com.bookxpert.employeemanager.domain.dsa.DuplicateDetector
import com.bookxpert.employeemanager.domain.model.Employee

object FormValidator {

    private val EMAIL_REGEX = Regex(
        "[a-zA-Z0-9+._%\\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"
    )

    fun validateFullName(name: String, existingEmployees: List<Employee> = emptyList(), currentId: Long? = null): String? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return "Full name is required"
        if (trimmed.length < 3) return "Full name must be at least 3 characters"
        val isDuplicateName = existingEmployees.any {
            it.id != currentId && it.fullName.trim().equals(trimmed, ignoreCase = true)
        }
        if (isDuplicateName) return "An employee with this name already exists"
        return null
    }

    fun validateEmail(email: String, duplicateDetector: DuplicateDetector? = null, existingList: List<Employee> = emptyList(), currentId: Long? = null): String? {
        val trimmed = email.trim()
        if (trimmed.isEmpty()) return "Email address is required"
        if (!EMAIL_REGEX.matches(trimmed)) return "Please enter a valid email address"
        if (duplicateDetector != null && duplicateDetector.isEmailDuplicate(trimmed, existingList, currentId)) {
            return "This email is already registered to another employee"
        }
        return null
    }

    fun validatePhone(phone: String, duplicateDetector: DuplicateDetector? = null, existingList: List<Employee> = emptyList(), currentId: Long? = null): String? {
        val trimmed = phone.trim()
        if (trimmed.isEmpty()) return "Phone number is required"
        val normalized = PhoneNormalizer.normalize(trimmed)
        if (normalized.length != 10 || !normalized.all { it.isDigit() }) {
            return "Phone number must be exactly 10 digits"
        }
        if (duplicateDetector != null && duplicateDetector.isPhoneDuplicate(trimmed, existingList, currentId)) {
            return "This phone number is already registered to another employee"
        }
        return null
    }

    fun validateAddress(address: String): String? {
        val trimmed = address.trim()
        if (trimmed.isEmpty()) return "Address is required"
        if (trimmed.length < 6) return "Address must be at least 6 characters"
        return null
    }

    fun validateSkills(skills: List<String>): String? {
        if (skills.isEmpty()) return "Select at least 1 skill"
        return null
    }

    fun validateJoiningDate(epochMillis: Long, currentEpochMillis: Long): String? {
        if (epochMillis <= 0) return "Joining date is required"
        if (epochMillis > currentEpochMillis + 86400000) {
            return "Joining date cannot be in the future"
        }
        return null
    }

    fun validateSalary(salaryString: String): String? {
        val trimmed = salaryString.trim().replace(",", "")
        if (trimmed.isEmpty()) return "Salary is required"
        val parsed = trimmed.toDoubleOrNull()
        if (parsed == null || parsed <= 0.0) return "Salary must be a positive number"
        return null
    }
}
