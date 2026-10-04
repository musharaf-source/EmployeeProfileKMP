package com.bookxpert.employeemanager.data.local.database

import androidx.room.TypeConverter

class DatabaseConverters {

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data.isNullOrBlank()) return emptyList()
        return data.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
}
