package com.bookxpert.employeemanager.presentation.navigation

sealed class Screen(val route: String) {
    data object EmployeeList : Screen("employee_list")
    data object AddEmployee : Screen("add_employee")
    data object EditEmployee : Screen("edit_employee/{id}") {
        fun createRoute(id: Long) = "edit_employee/$id"
    }
    data object EmployeeDetail : Screen("employee_detail/{id}") {
        fun createRoute(id: Long) = "employee_detail/$id"
    }
    data object TopEarners : Screen("top_earners")
}
