package com.bookxpert.employeemanager.data.repository

import kotlinx.datetime.Clock

object SystemTime {
    fun nowEpochMillis(): Long = Clock.System.now().toEpochMilliseconds()
}
