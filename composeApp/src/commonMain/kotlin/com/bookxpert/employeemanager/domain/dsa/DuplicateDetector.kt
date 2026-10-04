package com.bookxpert.employeemanager.domain.dsa

import com.bookxpert.employeemanager.domain.model.Employee
import com.bookxpert.employeemanager.domain.util.PhoneNormalizer

/**
 * In-memory index using HashSets for instant duplicate checking before hitting SQLite.
 *
 * Time Complexity:
 * - isEmailDuplicate: O(1) average lookup
 * - isPhoneDuplicate: O(1) average lookup
 * - insert / remove: O(1) average
 *
 * Space Complexity:
 * - O(N) where N is the total number of employee records loaded.
 */
class DuplicateDetector {

    private val emailIndex = HashSet<String>()
    private val phoneIndex = HashSet<String>()

    /**
     * Hydrates the in-memory hash sets with existing records from the database.
     */
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

    /**
     * Checks if an email is already assigned to another employee.
     * When [excludeEmployeeId] is provided (edit mode), matches for the current user are ignored.
     */
    fun isEmailDuplicate(email: String, existingList: List<Employee> = emptyList(), excludeEmployeeId: Long? = null): Boolean {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isEmpty()) return false

        if (excludeEmployeeId == null) {
            return emailIndex.contains(cleanEmail)
        }
        return existingList.any { it.id != excludeEmployeeId && it.email.trim().equals(cleanEmail, ignoreCase = true) }
    }

    /**
     * Checks if a phone number is already assigned to another employee.
     * When [excludeEmployeeId] is provided (edit mode), matches for the current user are ignored.
     */
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
