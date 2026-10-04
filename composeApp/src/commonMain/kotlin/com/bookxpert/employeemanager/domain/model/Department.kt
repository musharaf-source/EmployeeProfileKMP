package com.bookxpert.employeemanager.domain.model

enum class Department(val displayName: String) {
    ENGINEERING("Engineering"),
    HR("HR"),
    SALES("Sales"),
    FINANCE("Finance"),
    DESIGN("Design"),
    OPS("Ops");

    companion object {
        fun fromString(value: String): Department {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) }
                ?: ENGINEERING
        }
    }
}
