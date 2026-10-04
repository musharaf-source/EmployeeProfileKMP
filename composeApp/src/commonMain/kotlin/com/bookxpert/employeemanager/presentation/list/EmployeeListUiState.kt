package com.bookxpert.employeemanager.presentation.list

import com.bookxpert.employeemanager.domain.model.Employee
import com.bookxpert.employeemanager.presentation.list.components.FilterCriteria
import com.bookxpert.employeemanager.presentation.list.components.SortOption

data class EmployeeListUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val allEmployees: List<Employee> = emptyList(),
    val filteredEmployees: List<Employee> = emptyList(),
    val filterCriteria: FilterCriteria = FilterCriteria(),
    val sortOption: SortOption = SortOption.JOINING_DATE_DESC,
    val isFilterSheetVisible: Boolean = false,
    val employeePendingDelete: Employee? = null,
    val undoableEmployee: Employee? = null,
    val showUndoSnackbar: Boolean = false
) {
    val totalCount: Int get() = allEmployees.size
    val filteredCount: Int get() = filteredEmployees.size
    val isSearchingOrFiltering: Boolean
        get() = searchQuery.isNotBlank() || filterCriteria.isFilterActive
}
