package com.bookxpert.employeemanager.presentation.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bookxpert.employeemanager.data.repository.SystemTime
import com.bookxpert.employeemanager.domain.model.Department
import com.bookxpert.employeemanager.domain.model.EmploymentType
import com.bookxpert.employeemanager.domain.model.Gender
import com.bookxpert.employeemanager.platform.DocumentPickerResult
import com.bookxpert.employeemanager.platform.ImageSource
import com.bookxpert.employeemanager.platform.PlatformUtils
import com.bookxpert.employeemanager.platform.rememberDocumentPickerLauncher
import com.bookxpert.employeemanager.platform.rememberImagePickerLauncher
import com.bookxpert.employeemanager.presentation.components.DocumentUploadCard
import com.bookxpert.employeemanager.presentation.components.EmployeeAvatar
import com.bookxpert.employeemanager.presentation.components.EmployeeDatePickerDialog
import com.bookxpert.employeemanager.presentation.components.EmployeeDropdown
import com.bookxpert.employeemanager.presentation.components.EmployeeTextField
import com.bookxpert.employeemanager.presentation.components.SkillSelector
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeFormScreen(
    employeeId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: EmployeeFormViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(employeeId) {
        if (employeeId != null && employeeId > 0) {
            viewModel.loadEmployeeForEdit(employeeId)
        }
    }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.setSnackbarMessage(null)
        }
    }

    val imagePicker = rememberImagePickerLauncher { path ->
        viewModel.onProfileImageSelected(path)
    }

    val docPicker = rememberDocumentPickerLauncher { result ->
        when (result) {
            is DocumentPickerResult.Success -> viewModel.onResumeDocumentSelected(result.metadata)
            is DocumentPickerResult.Error -> viewModel.setSnackbarMessage(result.message)
            DocumentPickerResult.Cancelled -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditMode) "Edit Employee Profile" else "Add New Employee",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image Header
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                EmployeeAvatar(
                    fullName = uiState.fullName,
                    imagePath = uiState.profileImagePath,
                    size = 96.dp,
                    showCameraOverlay = true,
                    onAvatarClick = { viewModel.setShowImagePickerSheet(true) }
                )
            }

            Text(
                text = if (uiState.isEditMode) "Update profile details" else "Fill employee details below",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Full Name
            EmployeeTextField(
                value = uiState.fullName,
                onValueChange = { viewModel.onFullNameChanged(it) },
                label = "Full Name*",
                placeholder = "e.g. John Doe",
                leadingIcon = Icons.Default.Person,
                errorMessage = uiState.fullNameError,
                onFocusLost = { viewModel.onFullNameBlur() }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Email
            EmployeeTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                label = "Email Address*",
                placeholder = "john.doe@company.com",
                leadingIcon = Icons.Default.Email,
                errorMessage = uiState.emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                onFocusLost = { viewModel.onEmailBlur() }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Phone Number
            EmployeeTextField(
                value = uiState.phone,
                onValueChange = { viewModel.onPhoneChanged(it) },
                label = "Phone Number (10 digits)*",
                placeholder = "9876543210",
                leadingIcon = Icons.Default.Phone,
                errorMessage = uiState.phoneError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                onFocusLost = { viewModel.onPhoneBlur() }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Address (Multi-line, 4 lines visible)
            EmployeeTextField(
                value = uiState.address,
                onValueChange = { viewModel.onAddressChanged(it) },
                label = "Address (Min 6 chars)*",
                placeholder = "Street name, City, State, ZIP",
                leadingIcon = Icons.Default.LocationOn,
                errorMessage = uiState.addressError,
                singleLine = false,
                minLines = 4,
                maxLines = 6,
                onFocusLost = { viewModel.onAddressBlur() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Gender Radio Group
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Gender*",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Gender.entries.forEach { gender ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .selectable(
                                    selected = (uiState.gender == gender),
                                    onClick = { viewModel.onGenderSelected(gender) },
                                    role = Role.RadioButton
                                )
                                .padding(end = 8.dp)
                        ) {
                            RadioButton(
                                selected = (uiState.gender == gender),
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = gender.displayName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Department Dropdown
            EmployeeDropdown(
                label = "Department*",
                options = Department.entries,
                selectedOption = uiState.department,
                onOptionSelected = { viewModel.onDepartmentSelected(it) },
                optionLabel = { it.displayName }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Skills Selector
            SkillSelector(
                selectedSkills = uiState.selectedSkills,
                onSkillToggled = { viewModel.onSkillToggled(it) },
                errorMessage = uiState.skillsError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Employment Type Dropdown
            EmployeeDropdown(
                label = "Employment Type*",
                options = EmploymentType.entries,
                selectedOption = uiState.employmentType,
                onOptionSelected = { viewModel.onEmploymentTypeSelected(it) },
                optionLabel = { it.displayName }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Joining Date Picker
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Joining Date*",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedButton(
                    onClick = { viewModel.setShowDatePicker(true) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Select date",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (uiState.joiningDateEpochMillis > 0)
                                PlatformUtils.formatDate(uiState.joiningDateEpochMillis)
                            else "Select Joining Date",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (uiState.joiningDateError != null) {
                    Text(
                        text = uiState.joiningDateError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Salary Field
            EmployeeTextField(
                value = uiState.salary,
                onValueChange = { viewModel.onSalaryChanged(it) },
                label = "Annual Salary*",
                placeholder = "750000",
                prefixText = "₹ ",
                errorMessage = uiState.salaryError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onFocusLost = { viewModel.onSalaryBlur() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Active Status Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Active Employee",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Mark whether this employee is actively working",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = uiState.isActive,
                    onCheckedChange = { viewModel.onActiveToggled(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Document Upload Section
            DocumentUploadCard(
                document = uiState.resumeDocument,
                onUploadClick = { docPicker.launch() },
                onRemoveClick = { viewModel.onRemoveResume() }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Submit Button (Disabled until form is valid)
            Button(
                onClick = {
                    viewModel.saveEmployee {
                        onNavigateBack()
                    }
                },
                enabled = uiState.isFormValid && !uiState.isSaving,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = if (uiState.isEditMode) "Save Changes" else "Create Employee",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Date Picker Modal
        if (uiState.showDatePicker) {
            EmployeeDatePickerDialog(
                initialDateEpochMillis = uiState.joiningDateEpochMillis,
                maxDateEpochMillis = SystemTime.nowEpochMillis(),
                onDateSelected = { viewModel.onJoiningDateSelected(it) },
                onDismiss = { viewModel.setShowDatePicker(false) }
            )
        }

        // Image Picker Bottom Sheet
        if (uiState.showImagePickerSheet) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.setShowImagePickerSheet(false) },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Choose Profile Photo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.setShowImagePickerSheet(false)
                                imagePicker.launch(ImageSource.CAMERA)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Take Photo")
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        OutlinedButton(
                            onClick = {
                                viewModel.setShowImagePickerSheet(false)
                                imagePicker.launch(ImageSource.GALLERY)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Choose Gallery")
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
