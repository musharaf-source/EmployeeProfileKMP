package com.bookxpert.employeemanager.dsa

import com.bookxpert.employeemanager.domain.dsa.DuplicateDetector
import com.bookxpert.employeemanager.domain.model.Department
import com.bookxpert.employeemanager.domain.model.Employee
import com.bookxpert.employeemanager.domain.model.EmploymentType
import com.bookxpert.employeemanager.domain.model.Gender
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DuplicateDetectorTest {

    private lateinit var detector: DuplicateDetector
    private val seedList = listOf(
        Employee(
            id = 1,
            fullName = "John Doe",
            email = "john.doe@bookxpert.com",
            phoneNumber = "+91 9876543210",
            normalizedPhone = "9876543210",
            address = "123 Tech Park",
            gender = Gender.MALE,
            department = Department.ENGINEERING,
            skills = listOf("Kotlin"),
            employmentType = EmploymentType.FULL_TIME,
            isActive = true,
            joiningDateEpochMillis = 1700000000000L,
            salary = 850000.0
        ),
        Employee(
            id = 2,
            fullName = "Jane Smith",
            email = "jane.smith@bookxpert.com",
            phoneNumber = "9123456780",
            normalizedPhone = "9123456780",
            address = "456 Silicon Ave",
            gender = Gender.FEMALE,
            department = Department.DESIGN,
            skills = listOf("Compose"),
            employmentType = EmploymentType.FULL_TIME,
            isActive = true,
            joiningDateEpochMillis = 1701000000000L,
            salary = 920000.0
        )
    )

    @BeforeTest
    fun setup() {
        detector = DuplicateDetector()
        detector.seed(seedList)
    }

    @Test
    fun isEmailDuplicate_detectsExactAndCaseInsensitiveMatches() {
        assertTrue(detector.isEmailDuplicate("john.doe@bookxpert.com"))
        assertTrue(detector.isEmailDuplicate("JOHN.DOE@BOOKXPERT.COM"))
        assertTrue(detector.isEmailDuplicate("  jane.smith@bookxpert.com "))
        assertFalse(detector.isEmailDuplicate("new.user@bookxpert.com"))
    }

    @Test
    fun isPhoneDuplicate_detectsMatchesRegardlessOfFormatting() {
        assertTrue(detector.isPhoneDuplicate("9876543210"))
        assertTrue(detector.isPhoneDuplicate("+91 98765-43210"))
        assertTrue(detector.isPhoneDuplicate("09123456780"))
        assertFalse(detector.isPhoneDuplicate("9999999999"))
    }

    @Test
    fun isDuplicate_allowsSelfInEditMode() {
        assertFalse(detector.isEmailDuplicate("john.doe@bookxpert.com", seedList, excludeEmployeeId = 1))
        assertFalse(detector.isPhoneDuplicate("+91 9876543210", seedList, excludeEmployeeId = 1))
        assertTrue(detector.isEmailDuplicate("jane.smith@bookxpert.com", seedList, excludeEmployeeId = 1))
    }

    @Test
    fun dynamicIndexUpdates_reflectOnAddAndRemove() {
        val newEmp = Employee(
            id = 3,
            fullName = "Alice Green",
            email = "alice@bookxpert.com",
            phoneNumber = "8888888888",
            normalizedPhone = "8888888888",
            address = "789 Park St",
            gender = Gender.FEMALE,
            department = Department.HR,
            skills = listOf("KMP"),
            employmentType = EmploymentType.FULL_TIME,
            isActive = true,
            joiningDateEpochMillis = 1702000000000L,
            salary = 700000.0
        )

        detector.onEmployeeAdded(newEmp)
        assertTrue(detector.isEmailDuplicate("alice@bookxpert.com"))

        detector.onEmployeeRemoved(newEmp)
        assertFalse(detector.isEmailDuplicate("alice@bookxpert.com"))
    }
}
