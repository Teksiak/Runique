package com.teksiak.run.presentation.run_overview

sealed interface RunOverviewAction {
    data object OnStartRunClick : RunOverviewAction
    data object OnLogoutClick : RunOverviewAction
    data object OnAnalyticsClick : RunOverviewAction
}