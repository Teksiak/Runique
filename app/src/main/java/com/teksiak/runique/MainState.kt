package com.teksiak.runique

data class MainState(
    val isLoggedIn: Boolean = false,
    val isCheckingAuth: Boolean = true,
    val showAnalyticsInstallDialog: Boolean = false
)
