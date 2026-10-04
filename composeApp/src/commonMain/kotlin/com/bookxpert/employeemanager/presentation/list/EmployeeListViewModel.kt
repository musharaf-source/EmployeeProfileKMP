package com.bookxpert.employeemanager.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookxpert.employeemanager.domain.dsa.DuplicateDetector
import com.bookxpert.employeemanager.domain.dsa.TrieSearchIndex
import com.bookxpert.employeemanager.domain.dsa.UndoStack
import com.bookxpert.employeemanager.domain.model.Employee
import com.bookxpert.employeemanager.domain.repository.EmployeeRepository
import com.bookxpert.employeemanager.presentation.list.components.ActiveStatusFilter
import com.bookxpert.employeemanager.presentation.list.components.FilterCriteria
import com.bookxpert.employeemanager.presentation.list.components.SortOption
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class EmployeeListViewModel(
    private val repository: EmployeeRepository,
    private val duplicateDetector: DuplicateDetector,
    private val undoStack: UndoStack,
    private val trieSearchIndex: TrieSearchIndex
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _filterCriteria = MutableStateFlow(FilterCriteria())
    private val _sortOption = MutableStateFlow(SortOption.JOINING_DATE_DESC)
    private val _isFilterSheetVisible = MutableStateFlow(false)
    private val _employeePendingDelete = MutableStateFlow<Employee?>(null)
    private val _undoableEmployee = MutableStateFlow<Employee?>(null)
    private val _showUndoSnackbar = MutableStateFlow(false)

    // Debounce search queries by 300ms for smooth UI rendering
    private val debouncedSearchQuery = _searchQuery.debounce(300)

    val uiState: StateFlow<EmployeeListUiState> = combine(
        repository.getAllEmployees(),
        _searchQuery,
        debouncedSearchQuery,
        _filterCriteria,
        _sortOption,
        _isFilterSheetVisible,
        _employeePendingDelete,
        _undoableEmployee,
        _showUndoSnackbar
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val rawList = args[0] as List<Employee>
        val currentSearch = args[1] as String
        val debouncedSearch = args[2] as String
        val filter = args[3] as FilterCriteria
        val sort = args[4] as SortOption
        val isSheetVisible = args[5] as Boolean
        val pendingDelete = args[6] as Employee?
        val undoable = args[7] as Employee?
        val showUndo = args[8] as Boolean

        // Update in-memory search and duplicate indices
        duplicateDetector.seed(rawList)
        rebuildTrieIndex(rawList)

        val processedList = processList(rawList, currentSearch, filter, sort)

        EmployeeListUiState(
            isLoading = false,
            searchQuery = currentSearch,
            allEmployees = rawList,
            filteredEmployees = processedList,
            filterCriteria = filter,
            sortOption = sort,
            isFilterSheetVisible = isSheetVisible,
            employeePendingDelete = pendingDelete,
            undoableEmployee = undoable,
            showUndoSnackbar = showUndo
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EmployeeListUiState(isLoading = true)
    )

    private fun rebuildTrieIndex(employees: List<Employee>) {
        trieSearchIndex.clear()
        for (emp in employees) {
            trieSearchIndex.insert(emp.id, emp.fullName, emp.email, emp.department.displayName)
        }
    }

    private fun processList(
        rawList: List<Employee>,
        searchQuery: String,
        filter: FilterCriteria,
        sort: SortOption
    ): List<Employee> {
        val query = searchQuery.trim().lowercase()

        // 1. Filter by Search Query (Simultaneous check across Name, Email, Department)
        val searchFiltered = if (query.isEmpty()) {
            rawList
        } else {
            val trieMatches = trieSearchIndex.searchPrefix(query)
            rawList.filter { emp ->
                trieMatches.contains(emp.id) ||
                emp.fullName.lowercase().contains(query) ||
                emp.email.lowercase().contains(query) ||
                emp.department.displayName.lowercase().contains(query)
            }
        }

        // 2. Filter by Criteria (AND composition)
        val criteriaFiltered = searchFiltered.filter { emp ->
            // Department filter
            val matchesDept = filter.selectedDepartments.isEmpty() || filter.selectedDepartments.contains(emp.department)
            // Status filter
            val matchesStatus = when (filter.statusFilter) {
                ActiveStatusFilter.ALL -> true
                ActiveStatusFilter.ACTIVE_ONLY -> emp.isActive
                ActiveStatusFilter.INACTIVE_ONLY -> !emp.isActive
            }
            // Employment Type filter
            val matchesEmpType = filter.selectedEmploymentTypes.isEmpty() || filter.selectedEmploymentTypes.contains(emp.employmentType)

            matchesDept && matchesStatus && matchesEmpType
        }

        // 3. Sort Order
        return when (sort) {
            SortOption.NAME_ASC -> criteriaFiltered.sortedBy { it.fullName.lowercase() }
            SortOption.NAME_DESC -> criteriaFiltered.sortedByDescending { it.fullName.lowercase() }
            SortOption.JOINING_DATE_DESC -> criteriaFiltered.sortedByDescending { it.joiningDateEpochMillis }
            SortOption.JOINING_DATE_ASC -> criteriaFiltered.sortedBy { it.joiningDateEpochMillis }
            SortOption.SALARY_DESC -> criteriaFiltered.sortedByDescending { it.salary }
            SortOption.SALARY_ASC -> criteriaFiltered.sortedBy { it.salary }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFilterCriteriaChanged(criteria: FilterCriteria) {
        _filterCriteria.value = criteria
    }

    fun onSortOptionChanged(option: SortOption) {
        _sortOption.value = option
    }

    fun setFilterSheetVisible(visible: Boolean) {
        _isFilterSheetVisible.value = visible
    }

    fun requestDeleteConfirmation(employee: Employee) {
        _employeePendingDelete.value = employee
    }

    fun dismissDeleteConfirmation() {
        _employeePendingDelete.value = null
    }

    fun confirmDelete() {
        val employee = _employeePendingDelete.value ?: return
        _employeePendingDelete.value = null

        viewModelScope.launch {
            repository.deleteEmployee(employee)
            duplicateDetector.onEmployeeRemoved(employee)
            undoStack.push(employee)

            _undoableEmployee.value = employee
            _showUndoSnackbar.value = true
        }
    }

    fun undoDelete() {
        val lastDeleted = undoStack.pop() ?: return
        viewModelScope.launch {
            repository.insertEmployee(lastDeleted)
            duplicateDetector.onEmployeeAdded(lastDeleted)
            _showUndoSnackbar.value = false
            _undoableEmployee.value = null
        }
    }

    fun dismissUndoSnackbar() {
        _showUndoSnackbar.value = false
        _undoableEmployee.value = null
    }
}
