package com.bookxpert.employeemanager.presentation.form

import com.bookxpert.employeemanager.domain.model.Department
import com.bookxpert.employeemanager.domain.model.DocumentMetadata
import com.bookxpert.employeemanager.domain.model.EmploymentType
import com.bookxpert.employeemanager.domain.model.Gender

data class EmployeeFormUiState(
    val employeeId: Long? = null,
    val isEditMode: Boolean = false,
    val fullName: String = "",
    val fullNameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val phone: String = "",
    val phoneError: String? = null,
    val address: String = "",
    val addressError: String? = null,
    val gender: Gender = Gender.MALE,
    val department: Department = Department.ENGINEERING,
    val selectedSkills: List<String> = listOf("Kotlin", "Compose"),
    val skillsError: String? = null,
    val employmentType: EmploymentType = EmploymentType.FULL_TIME,
    val isActive: Boolean = true,
    val joiningDateEpochMillis: Long = 0L,
    val joiningDateError: String? = null,
    val salary: String = "",
    val salaryError: String? = null,
    val profileImagePath: String? = null,
    val resumeDocument: DocumentMetadata? = null,
    val snackbarMessage: String? = null,
    val isSaving: Boolean = false,
    val isSavedSuccess: Boolean = false,
    val showDatePicker: Boolean = false,
    val showImagePickerSheet: Boolean = false
) {
    val isFormValid: Boolean
        get() = fullName.trim().length >= 3 &&
                fullNameError == null &&
                email.isNotBlank() &&
                emailError == null &&
                phone.isNotBlank() &&
                phoneError == null &&
                address.trim().length >= 6 &&
                addressError == null &&
                selectedSkills.isNotEmpty() &&
                skillsError == null &&
                joiningDateEpochMillis > 0 &&
                joiningDateError == null &&
                (salary.toDoubleOrNull() ?: 0.0) > 0.0 &&
                salaryError == null
}
