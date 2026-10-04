package com.bookxpert.employeemanager

import androidx.compose.ui.window.ComposeUIViewController
import com.bookxpert.employeemanager.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        enforceStrictPlistSanityCheck = false
        initKoin()
    }
) {
    App()
}
