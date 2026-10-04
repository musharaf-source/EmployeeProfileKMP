package com.bookxpert.employeemanager

import androidx.compose.runtime.Composable
import com.bookxpert.employeemanager.presentation.navigation.AppNavHost
import com.bookxpert.employeemanager.presentation.theme.EmployeeTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        EmployeeTheme {
            AppNavHost()
        }
    }
}
