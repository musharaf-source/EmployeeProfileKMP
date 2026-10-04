package com.bookxpert.employeemanager.dsa

import com.bookxpert.employeemanager.domain.dsa.topNBySalary
import com.bookxpert.employeemanager.domain.model.Department
import com.bookxpert.employeemanager.domain.model.Employee
import com.bookxpert.employeemanager.domain.model.EmploymentType
import com.bookxpert.employeemanager.domain.model.Gender
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SalaryHeapTest {

    private fun createEmployee(id: Long, name: String, salary: Double): Employee {
        return Employee(
            id = id,
            fullName = name,
            email = "$name@company.com".lowercase(),
            phoneNumber = "900000000$id",
            normalizedPhone = "900000000$id",
            address = "Office St",
            gender = Gender.MALE,
            department = Department.ENGINEERING,
            skills = listOf("Kotlin"),
            employmentType = EmploymentType.FULL_TIME,
            isActive = true,
            joiningDateEpochMillis = 1700000000000L,
            salary = salary
        )
    }

    @Test
    fun topNBySalary_returnsCorrectTop5InDescendingOrder() {
        val employees = listOf(
            createEmployee(1, "Emp 50k", 50000.0),
            createEmployee(2, "Emp 120k", 120000.0),
            createEmployee(3, "Emp 30k", 30000.0),
            createEmployee(4, "Emp 200k", 200000.0),
            createEmployee(5, "Emp 80k", 80000.0),
            createEmployee(6, "Emp 150k", 150000.0),
            createEmployee(7, "Emp 90k", 90000.0)
        )

        val result = topNBySalary(employees, n = 5)

        assertEquals(5, result.size)
        assertEquals(200000.0, result[0].salary)
        assertEquals(150000.0, result[1].salary)
        assertEquals(120000.0, result[2].salary)
        assertEquals(90000.0, result[3].salary)
        assertEquals(80000.0, result[4].salary)
    }

    @Test
    fun topNBySalary_handlesDatasetSmallerThanN() {
        val employees = listOf(
            createEmployee(1, "A", 100000.0),
            createEmployee(2, "B", 200000.0)
        )

        val result = topNBySalary(employees, n = 5)
        assertEquals(2, result.size)
        assertEquals(200000.0, result[0].salary)
        assertEquals(100000.0, result[1].salary)
    }

    @Test
    fun topNBySalary_handlesEmptyDatasetAndZeroN() {
        val emptyResult = topNBySalary(emptyList(), n = 5)
        assertTrue(emptyResult.isEmpty())

        val zeroNResult = topNBySalary(listOf(createEmployee(1, "A", 100000.0)), n = 0)
        assertTrue(zeroNResult.isEmpty())
    }
}
