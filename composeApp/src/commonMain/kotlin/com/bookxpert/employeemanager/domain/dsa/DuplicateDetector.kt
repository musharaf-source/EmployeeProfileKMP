package com.bookxpert.employeemanager.domain.dsa

import com.bookxpert.employeemanager.domain.model.Employee
import com.bookxpert.employeemanager.domain.util.PhoneNormalizer

/**
 * Checks for duplicate email or normalised phone before persist.
 * Time complexity: O(1) average — HashSet contains()
 * Space complexity: O(n) where n = number of employees
 */
class DuplicateDetector {

    private val emailIndex = HashSet<String>()
    private val phoneIndex = HashSet<String>()

    // Seed in-memory sets from room cache on app start
    fun seed(employees: List<Employee>) {
        emailIndex.clear()
        phoneIndex.clear()
        for (emp in employees) {
            if (emp.email.isNotBlank()) {
                emailIndex.add(emp.email.trim().lowercase())
            }
            val normPhone = if (emp.normalizedPhone.isNotBlank()) {
                emp.normalizedPhone
            } else {
                PhoneNormalizer.normalize(emp.phoneNumber)
            }
            if (normPhone.isNotBlank()) {
                phoneIndex.add(normPhone)
            }
        }
    }

    // Fast O(1) check for emails; allows self in edit mode
    fun isEmailDuplicate(email: String, existingList: List<Employee> = emptyList(), excludeEmployeeId: Long? = null): Boolean {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isEmpty()) return false

        if (excludeEmployeeId == null) {
            return emailIndex.contains(cleanEmail)
        }
        return existingList.any { it.id != excludeEmployeeId && it.email.trim().equals(cleanEmail, ignoreCase = true) }
    }

    // Fast O(1) check for normalized phones; allows self in edit mode
    fun isPhoneDuplicate(rawPhone: String, existingList: List<Employee> = emptyList(), excludeEmployeeId: Long? = null): Boolean {
        val normPhone = PhoneNormalizer.normalize(rawPhone)
        if (normPhone.isEmpty()) return false

        if (excludeEmployeeId == null) {
            return phoneIndex.contains(normPhone)
        }
        return existingList.any { 
            it.id != excludeEmployeeId && 
            (it.normalizedPhone == normPhone || PhoneNormalizer.normalize(it.phoneNumber) == normPhone)
        }
    }

    fun onEmployeeAdded(employee: Employee) {
        if (employee.email.isNotBlank()) {
            emailIndex.add(employee.email.trim().lowercase())
        }
        val norm = PhoneNormalizer.normalize(employee.phoneNumber)
        if (norm.isNotBlank()) {
            phoneIndex.add(norm)
        }
    }

    fun onEmployeeRemoved(employee: Employee) {
        emailIndex.remove(employee.email.trim().lowercase())
        val norm = PhoneNormalizer.normalize(employee.phoneNumber)
        phoneIndex.remove(norm)
    }

    fun onEmployeeUpdated(oldEmployee: Employee, newEmployee: Employee) {
        onEmployeeRemoved(oldEmployee)
        onEmployeeAdded(newEmployee)
    }
}
