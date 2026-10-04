package com.bookxpert.employeemanager.domain.model

enum class EmploymentType(val displayName: String) {
    FULL_TIME("Full-Time"),
    PART_TIME("Part-Time"),
    CONTRACT("Contract");

    companion object {
        fun fromString(value: String): EmploymentType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) }
                ?: FULL_TIME
        }
    }
}
