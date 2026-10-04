package com.bookxpert.employeemanager.presentation.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookxpert.employeemanager.data.repository.SystemTime
import com.bookxpert.employeemanager.domain.dsa.DuplicateDetector
import com.bookxpert.employeemanager.domain.model.Department
import com.bookxpert.employeemanager.domain.model.DocumentMetadata
import com.bookxpert.employeemanager.domain.model.Employee
import com.bookxpert.employeemanager.domain.model.EmploymentType
import com.bookxpert.employeemanager.domain.model.Gender
import com.bookxpert.employeemanager.domain.repository.EmployeeRepository
import com.bookxpert.employeemanager.domain.util.FormValidator
import com.bookxpert.employeemanager.domain.util.PhoneNormalizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployeeFormViewModel(
    private val repository: EmployeeRepository,
    private val duplicateDetector: DuplicateDetector
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeFormUiState(joiningDateEpochMillis = SystemTime.nowEpochMillis()))
    val uiState: StateFlow<EmployeeFormUiState> = _uiState.asStateFlow()

    private var allEmployeesCache: List<Employee> = emptyList()

    init {
        viewModelScope.launch {
            allEmployeesCache = repository.getAllEmployees().first()
            duplicateDetector.seed(allEmployeesCache)
        }
    }

    fun loadEmployeeForEdit(id: Long) {
        viewModelScope.launch {
            val employee = repository.getEmployeeById(id) ?: return@launch
            _uiState.update {
                it.copy(
                    employeeId = employee.id,
                    isEditMode = true,
                    fullName = employee.fullName,
                    email = employee.email,
                    phone = employee.phoneNumber,
                    address = employee.address,
                    gender = employee.gender,
                    department = employee.department,
                    selectedSkills = employee.skills,
                    employmentType = employee.employmentType,
                    isActive = employee.isActive,
                    joiningDateEpochMillis = employee.joiningDateEpochMillis,
                    salary = if (employee.salary % 1.0 == 0.0) employee.salary.toLong().toString() else employee.salary.toString(),
                    profileImagePath = employee.profileImagePath,
                    resumeDocument = employee.resumeDocument
                )
            }
        }
    }

    fun onFullNameChanged(value: String) {
        _uiState.update { it.copy(fullName = value, fullNameError = null) }
    }

    fun onFullNameBlur() {
        val state = _uiState.value
        val error = FormValidator.validateFullName(state.fullName, allEmployeesCache, state.employeeId)
        _uiState.update { it.copy(fullNameError = error) }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onEmailBlur() {
        val state = _uiState.value
        val error = FormValidator.validateEmail(state.email, duplicateDetector, allEmployeesCache, state.employeeId)
        _uiState.update { it.copy(emailError = error) }
    }

    fun onPhoneChanged(value: String) {
        val filtered = value.filter { it.isDigit() || it in "+- ()" }
        _uiState.update { it.copy(phone = filtered, phoneError = null) }
    }

    fun onPhoneBlur() {
        val state = _uiState.value
        val error = FormValidator.validatePhone(state.phone, duplicateDetector, allEmployeesCache, state.employeeId)
        _uiState.update { it.copy(phoneError = error) }
    }

    fun onAddressChanged(value: String) {
        _uiState.update { it.copy(address = value, addressError = null) }
    }

    fun onAddressBlur() {
        val state = _uiState.value
        val error = FormValidator.validateAddress(state.address)
        _uiState.update { it.copy(addressError = error) }
    }

    fun onGenderSelected(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun onDepartmentSelected(department: Department) {
        _uiState.update { it.copy(department = department) }
    }

    fun onSkillToggled(skillName: String) {
        val current = _uiState.value.selectedSkills.toMutableList()
        if (current.contains(skillName)) {
            current.remove(skillName)
        } else {
            current.add(skillName)
        }
        val error = FormValidator.validateSkills(current)
        _uiState.update { it.copy(selectedSkills = current, skillsError = error) }
    }

    fun onEmploymentTypeSelected(type: EmploymentType) {
        _uiState.update { it.copy(employmentType = type) }
    }

    fun onActiveToggled(active: Boolean) {
        _uiState.update { it.copy(isActive = active) }
    }

    fun onJoiningDateSelected(epochMillis: Long) {
        val error = FormValidator.validateJoiningDate(epochMillis, SystemTime.nowEpochMillis())
        _uiState.update {
            it.copy(
                joiningDateEpochMillis = epochMillis,
                joiningDateError = error,
                showDatePicker = false
            )
        }
    }

    fun onSalaryChanged(value: String) {
        val filtered = value.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(salary = filtered, salaryError = null) }
    }

    fun onSalaryBlur() {
        val state = _uiState.value
        val error = FormValidator.validateSalary(state.salary)
        _uiState.update { it.copy(salaryError = error) }
    }

    fun onProfileImageSelected(path: String?) {
        _uiState.update { it.copy(profileImagePath = path, showImagePickerSheet = false) }
    }

    fun onResumeDocumentSelected(doc: DocumentMetadata) {
        _uiState.update { it.copy(resumeDocument = doc) }
    }

    fun onRemoveResume() {
        _uiState.update { it.copy(resumeDocument = null) }
    }

    fun setShowDatePicker(show: Boolean) {
        _uiState.update { it.copy(showDatePicker = show) }
    }

    fun setShowImagePickerSheet(show: Boolean) {
        _uiState.update { it.copy(showImagePickerSheet = show) }
    }

    fun setSnackbarMessage(msg: String?) {
        _uiState.update { it.copy(snackbarMessage = msg) }
    }

    fun saveEmployee(onSuccess: () -> Unit) {
        val state = _uiState.value

        // Validate all form fields
        val nameErr = FormValidator.validateFullName(state.fullName, allEmployeesCache, state.employeeId)
        val emailErr = FormValidator.validateEmail(state.email, duplicateDetector, allEmployeesCache, state.employeeId)
        val phoneErr = FormValidator.validatePhone(state.phone, duplicateDetector, allEmployeesCache, state.employeeId)
        val addressErr = FormValidator.validateAddress(state.address)
        val skillsErr = FormValidator.validateSkills(state.selectedSkills)
        val dateErr = FormValidator.validateJoiningDate(state.joiningDateEpochMillis, SystemTime.nowEpochMillis())
        val salaryErr = FormValidator.validateSalary(state.salary)

        if (nameErr != null || emailErr != null || phoneErr != null || addressErr != null ||
            skillsErr != null || dateErr != null || salaryErr != null
        ) {
            _uiState.update {
                it.copy(
                    fullNameError = nameErr,
                    emailError = emailErr,
                    phoneError = phoneErr,
                    addressError = addressErr,
                    skillsError = skillsErr,
                    joiningDateError = dateErr,
                    salaryError = salaryErr,
                    snackbarMessage = "Please resolve the highlighted validation errors."
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val cleanEmail = state.email.trim().lowercase()
            val normalizedPhone = PhoneNormalizer.normalize(state.phone)
            val parsedSalary = state.salary.replace(",", "").toDoubleOrNull() ?: 0.0

            val employee = Employee(
                id = state.employeeId ?: 0,
                fullName = state.fullName.trim(),
                email = cleanEmail,
                phoneNumber = state.phone.trim(),
                normalizedPhone = normalizedPhone,
                address = state.address.trim(),
                gender = state.gender,
                department = state.department,
                skills = state.selectedSkills,
                employmentType = state.employmentType,
                isActive = state.isActive,
                joiningDateEpochMillis = state.joiningDateEpochMillis,
                salary = parsedSalary,
                profileImagePath = state.profileImagePath,
                resumeDocument = state.resumeDocument
            )

            if (state.isEditMode) {
                val old = repository.getEmployeeById(employee.id)
                repository.updateEmployee(employee)
                if (old != null) {
                    duplicateDetector.onEmployeeUpdated(old, employee)
                }
            } else {
                val newId = repository.insertEmployee(employee)
                duplicateDetector.onEmployeeAdded(employee.copy(id = newId))
            }

            _uiState.update { it.copy(isSaving = false, isSavedSuccess = true) }
            onSuccess()
        }
    }
}
