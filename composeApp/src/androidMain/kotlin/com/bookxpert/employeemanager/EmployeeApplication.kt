package com.bookxpert.employeemanager

import android.app.Application
import com.bookxpert.employeemanager.data.local.database.appContext
import com.bookxpert.employeemanager.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class EmployeeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
        initKoin {
            androidLogger()
            androidContext(this@EmployeeApplication)
        }
    }
}
