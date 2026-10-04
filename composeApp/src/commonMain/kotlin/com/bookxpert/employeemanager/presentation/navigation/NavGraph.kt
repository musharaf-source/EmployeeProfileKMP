package com.bookxpert.employeemanager.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bookxpert.employeemanager.presentation.detail.EmployeeDetailScreen
import com.bookxpert.employeemanager.presentation.form.EmployeeFormScreen
import com.bookxpert.employeemanager.presentation.list.EmployeeListScreen
import com.bookxpert.employeemanager.presentation.top_earners.TopEarnersScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.EmployeeList.route,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300)) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300)) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300)) }
    ) {
        composable(Screen.EmployeeList.route) {
            EmployeeListScreen(
                onNavigateToAdd = { navController.navigate(Screen.AddEmployee.route) },
                onNavigateToEdit = { id -> navController.navigate(Screen.EditEmployee.createRoute(id)) },
                onNavigateToDetail = { id -> navController.navigate(Screen.EmployeeDetail.createRoute(id)) },
                onNavigateToTopEarners = { navController.navigate(Screen.TopEarners.route) }
            )
        }

        composable(Screen.AddEmployee.route) {
            EmployeeFormScreen(
                employeeId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditEmployee.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val employeeId = backStackEntry.arguments?.getLong("id")
            EmployeeFormScreen(
                employeeId = employeeId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EmployeeDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val employeeId = backStackEntry.arguments?.getLong("id") ?: 0L
            EmployeeDetailScreen(
                employeeId = employeeId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate(Screen.EditEmployee.createRoute(id)) }
            )
        }

        composable(Screen.TopEarners.route) {
            TopEarnersScreen(
                onNavigateBack = { navController.popBackStack() },
                onEmployeeClick = { id -> navController.navigate(Screen.EmployeeDetail.createRoute(id)) }
            )
        }
    }
}
