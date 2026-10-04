package com.bookxpert.employeemanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bookxpert.employeemanager.data.local.entity.EmployeeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {

    @Query("SELECT * FROM employees ORDER BY createdAt DESC")
    fun getAllEmployees(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees WHERE id = :id LIMIT 1")
    suspend fun getEmployeeById(id: Long): EmployeeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: EmployeeEntity): Long

    @Update
    suspend fun updateEmployee(employee: EmployeeEntity)

    @Delete
    suspend fun deleteEmployee(employee: EmployeeEntity)

    @Query("DELETE FROM employees WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM employees ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    fun getPagedEmployees(limit: Int, offset: Int): Flow<List<EmployeeEntity>>

    @Query("SELECT COUNT(*) FROM employees")
    suspend fun getCount(): Int
}
