package com.bookxpert.employeemanager.domain.repository

import com.bookxpert.employeemanager.domain.model.Employee
import kotlinx.coroutines.flow.Flow

interface EmployeeRepository {
    fun getAllEmployees(): Flow<List<Employee>>
    suspend fun getEmployeeById(id: Long): Employee?
    suspend fun insertEmployee(employee: Employee): Long
    suspend fun updateEmployee(employee: Employee)
    suspend fun deleteEmployee(employee: Employee)
    fun getPagedEmployees(page: Int, pageSize: Int = 20): Flow<List<Employee>>
}
