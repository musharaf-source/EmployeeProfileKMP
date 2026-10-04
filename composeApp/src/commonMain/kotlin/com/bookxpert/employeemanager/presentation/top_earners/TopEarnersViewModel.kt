package com.bookxpert.employeemanager.presentation.top_earners

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookxpert.employeemanager.domain.dsa.topNBySalary
import com.bookxpert.employeemanager.domain.model.Employee
import com.bookxpert.employeemanager.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class TopEarnersUiState(
    val topCount: Int = 5,
    val topEarners: List<Employee> = emptyList(),
    val totalEmployees: Int = 0,
    val isLoading: Boolean = true
)

class TopEarnersViewModel(
    private val repository: EmployeeRepository
) : ViewModel() {

    private val _topCount = MutableStateFlow(5)

    val uiState: StateFlow<TopEarnersUiState> = combine(
        repository.getAllEmployees(),
        _topCount
    ) { employees, count ->
        val computed = topNBySalary(employees, count)
        TopEarnersUiState(
            topCount = count,
            topEarners = computed,
            totalEmployees = employees.size,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TopEarnersUiState(isLoading = true)
    )

    fun incrementCount() {
        if (_topCount.value < 10) {
            _topCount.value += 1
        }
    }

    fun decrementCount() {
        if (_topCount.value > 1) {
            _topCount.value -= 1
        }
    }
}
