package com.bookxpert.employeemanager.dsa

import com.bookxpert.employeemanager.domain.dsa.UndoStack
import com.bookxpert.employeemanager.domain.model.Department
import com.bookxpert.employeemanager.domain.model.Employee
import com.bookxpert.employeemanager.domain.model.EmploymentType
import com.bookxpert.employeemanager.domain.model.Gender
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UndoStackTest {

    private fun createDummy(id: Long) = Employee(
        id = id,
        fullName = "Employee $id",
        email = "emp$id@company.com",
        phoneNumber = "900000000$id",
        normalizedPhone = "900000000$id",
        address = "City",
        gender = Gender.MALE,
        department = Department.ENGINEERING,
        skills = listOf("Kotlin"),
        employmentType = EmploymentType.FULL_TIME,
        isActive = true,
        joiningDateEpochMillis = 1700000000000L,
        salary = 50000.0
    )

    @Test
    fun pushAndPop_followsLifoOrder() {
        val stack = UndoStack(maxCapacity = 10)
        val emp1 = createDummy(1)
        val emp2 = createDummy(2)

        stack.push(emp1)
        stack.push(emp2)

        assertTrue(stack.canUndo)
        assertEquals(2, stack.size)
        assertEquals(emp2, stack.pop())
        assertEquals(emp1, stack.pop())
        assertNull(stack.pop())
        assertFalse(stack.canUndo)
    }

    @Test
    fun push_dropsOldestWhenCapacityExceeded() {
        val stack = UndoStack(maxCapacity = 3)
        stack.push(createDummy(1))
        stack.push(createDummy(2))
        stack.push(createDummy(3))
        stack.push(createDummy(4)) // Should evict ID 1

        assertEquals(3, stack.size)
        assertEquals(4L, stack.pop()?.id)
        assertEquals(3L, stack.pop()?.id)
        assertEquals(2L, stack.pop()?.id)
        assertNull(stack.pop())
    }
}
