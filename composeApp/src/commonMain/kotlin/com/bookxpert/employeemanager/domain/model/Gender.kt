package com.bookxpert.employeemanager.domain.model

enum class Gender(val displayName: String) {
    MALE("Male"),
    FEMALE("Female"),
    PREFER_NOT_TO_SAY("Prefer not to say");

    companion object {
        fun fromString(value: String): Gender {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) }
                ?: PREFER_NOT_TO_SAY
        }
    }
}
