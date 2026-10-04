package com.bookxpert.employeemanager.di

import com.bookxpert.employeemanager.data.local.database.AppDatabase
import com.bookxpert.employeemanager.data.local.database.createRoomDatabase
import com.bookxpert.employeemanager.data.local.database.getDatabaseBuilder
import com.bookxpert.employeemanager.data.local.preferences.ThemePreferences
import com.bookxpert.employeemanager.data.repository.EmployeeRepositoryImpl
import com.bookxpert.employeemanager.domain.dsa.DuplicateDetector
import com.bookxpert.employeemanager.domain.dsa.TrieSearchIndex
import com.bookxpert.employeemanager.domain.dsa.UndoStack
import com.bookxpert.employeemanager.domain.repository.EmployeeRepository
import com.bookxpert.employeemanager.presentation.form.EmployeeFormViewModel
import com.bookxpert.employeemanager.presentation.list.EmployeeListViewModel
import com.bookxpert.employeemanager.presentation.top_earners.TopEarnersViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    single<AppDatabase> { createRoomDatabase(getDatabaseBuilder()) }
    single { get<AppDatabase>().employeeDao() }

    single<EmployeeRepository> { EmployeeRepositoryImpl(get()) }

    single { DuplicateDetector() }
    single { UndoStack(maxCapacity = 10) }
    single { TrieSearchIndex() }
    single { ThemePreferences() }

    viewModel { EmployeeListViewModel(get(), get(), get(), get()) }
    viewModel { EmployeeFormViewModel(get(), get()) }
    viewModel { TopEarnersViewModel(get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(appModule)
    }
