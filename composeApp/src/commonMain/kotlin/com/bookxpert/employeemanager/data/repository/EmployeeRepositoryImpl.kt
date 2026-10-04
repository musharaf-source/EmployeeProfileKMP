package com.bookxpert.employeemanager.data.repository

import com.bookxpert.employeemanager.data.local.dao.EmployeeDao
import com.bookxpert.employeemanager.data.local.entity.EmployeeEntity
import com.bookxpert.employeemanager.domain.model.Employee
import com.bookxpert.employeemanager.domain.repository.EmployeeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class EmployeeRepositoryImpl(
    private val dao: EmployeeDao
) : EmployeeRepository {

    override fun getAllEmployees(): Flow<List<Employee>> {
        return dao.getAllEmployees()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getEmployeeById(id: Long): Employee? {
        return withContext(Dispatchers.IO) {
            dao.getEmployeeById(id)?.toDomain()
        }
    }

    override suspend fun insertEmployee(employee: Employee): Long {
        return withContext(Dispatchers.IO) {
            val now = employee.createdAt.takeIf { it > 0 } ?: SystemTime.nowEpochMillis()
            val entity = EmployeeEntity.fromDomain(employee.copy(createdAt = now, updatedAt = now))
            dao.insertEmployee(entity)
        }
    }

    override suspend fun updateEmployee(employee: Employee) {
        withContext(Dispatchers.IO) {
            val now = SystemTime.nowEpochMillis()
            val entity = EmployeeEntity.fromDomain(employee.copy(updatedAt = now))
            dao.updateEmployee(entity)
        }
    }

    override suspend fun deleteEmployee(employee: Employee) {
        withContext(Dispatchers.IO) {
            dao.deleteById(employee.id)
        }
    }

    override fun getPagedEmployees(page: Int, pageSize: Int): Flow<List<Employee>> {
        val offset = page * pageSize
        return dao.getPagedEmployees(limit = pageSize, offset = offset)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }
}
