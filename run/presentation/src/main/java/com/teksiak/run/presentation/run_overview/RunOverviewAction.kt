package com.teksiak.run.presentation.run_overview

sealed interface RunOverviewAction {
    data object OnCheckRunStatus: RunOverviewAction
    data object OnStartRunClick: RunOverviewAction
    data object OnLogoutClick: RunOverviewAction
    data object OnAnalyticsClick: RunOverviewAction
    data object OnDiscardRunClick: RunOverviewAction
    data object OnDismissDiscardRunDialogClick: RunOverviewAction
}