package com.bookxpert.employeemanager.data.local.preferences

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

class ThemePreferences {
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun toggleDarkMode(currentIsDark: Boolean) {
        _themeMode.value = if (currentIsDark) ThemeMode.LIGHT else ThemeMode.DARK
    }
}
