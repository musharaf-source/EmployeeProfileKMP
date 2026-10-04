package com.bookxpert.employeemanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Employee(
    val id: Long = 0,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val normalizedPhone: String,
    val address: String,
    val gender: Gender,
    val department: Department,
    val skills: List<String>,
    val employmentType: EmploymentType,
    val isActive: Boolean = true,
    val joiningDateEpochMillis: Long,
    val salary: Double,
    val profileImagePath: String? = null,
    val resumeDocument: DocumentMetadata? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
) {
    val initials: String
        get() {
            val words = fullName.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
            return when {
                words.isEmpty() -> "EM"
                words.size == 1 -> words[0].take(2).uppercase()
                else -> "${words[0].first()}${words[1].first()}".uppercase()
            }
        }
}
