package com.bookxpert.employeemanager.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.bookxpert.employeemanager.data.local.dao.EmployeeDao
import com.bookxpert.employeemanager.data.local.entity.EmployeeEntity

@Database(
    entities = [EmployeeEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun employeeDao(): EmployeeDao
}

// Room compiler generates implementation of this expect object
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
