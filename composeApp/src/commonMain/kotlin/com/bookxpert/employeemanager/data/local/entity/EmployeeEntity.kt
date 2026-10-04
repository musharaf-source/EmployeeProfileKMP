package com.bookxpert.employeemanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookxpert.employeemanager.domain.model.Department
import com.bookxpert.employeemanager.domain.model.DocumentMetadata
import com.bookxpert.employeemanager.domain.model.Employee
import com.bookxpert.employeemanager.domain.model.EmploymentType
import com.bookxpert.employeemanager.domain.model.Gender

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val normalizedPhone: String,
    val address: String,
    val gender: String,
    val department: String,
    val skills: String,
    val employmentType: String,
    val isActive: Boolean,
    val joiningDateEpochMillis: Long,
    val salary: Double,
    val profileImagePath: String?,
    val resumeName: String?,
    val resumeSizeBytes: Long?,
    val resumeMimeType: String?,
    val resumeLocalUri: String?,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): Employee {
        val doc = if (!resumeName.isNullOrBlank() && !resumeLocalUri.isNullOrBlank()) {
            DocumentMetadata(
                fileName = resumeName,
                fileSizeBytes = resumeSizeBytes ?: 0L,
                mimeType = resumeMimeType ?: "application/octet-stream",
                localUri = resumeLocalUri
            )
        } else null

        val skillList = if (skills.isBlank()) emptyList() else skills.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        return Employee(
            id = id,
            fullName = fullName,
            email = email,
            phoneNumber = phoneNumber,
            normalizedPhone = normalizedPhone,
            address = address,
            gender = Gender.fromString(gender),
            department = Department.fromString(department),
            skills = skillList,
            employmentType = EmploymentType.fromString(employmentType),
            isActive = isActive,
            joiningDateEpochMillis = joiningDateEpochMillis,
            salary = salary,
            profileImagePath = profileImagePath,
            resumeDocument = doc,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(employee: Employee): EmployeeEntity {
            return EmployeeEntity(
                id = employee.id,
                fullName = employee.fullName.trim(),
                email = employee.email.trim().lowercase(),
                phoneNumber = employee.phoneNumber.trim(),
                normalizedPhone = employee.normalizedPhone,
                address = employee.address.trim(),
                gender = employee.gender.name,
                department = employee.department.name,
                skills = employee.skills.joinToString(","),
                employmentType = employee.employmentType.name,
                isActive = employee.isActive,
                joiningDateEpochMillis = employee.joiningDateEpochMillis,
                salary = employee.salary,
                profileImagePath = employee.profileImagePath,
                resumeName = employee.resumeDocument?.fileName,
                resumeSizeBytes = employee.resumeDocument?.fileSizeBytes,
                resumeMimeType = employee.resumeDocument?.mimeType,
                resumeLocalUri = employee.resumeDocument?.localUri,
                createdAt = if (employee.createdAt == 0L) employee.joiningDateEpochMillis else employee.createdAt,
                updatedAt = employee.updatedAt
            )
        }
    }
}
