package com.bookxpert.employeemanager.domain.model

enum class Skill(val displayName: String) {
    KOTLIN("Kotlin"),
    COMPOSE("Compose"),
    KMP("KMP"),
    COROUTINES("Coroutines"),
    ROOM("Room DB"),
    KOIN("Koin DI"),
    SWIFT("Swift / iOS"),
    SQL("SQL"),
    SYSTEM_DESIGN("System Design"),
    CI_CD("CI/CD");

    companion object {
        fun fromString(value: String): Skill? {
            return entries.firstOrNull { 
                it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) 
            }
        }
    }
}
