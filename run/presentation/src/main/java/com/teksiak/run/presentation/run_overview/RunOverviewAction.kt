package com.teksiak.run.presentation.run_overview

import com.teksiak.run.presentation.run_overview.model.RunUi

sealed interface RunOverviewAction {
    data object OnStartRunClick: RunOverviewAction
    data object OnLogoutClick: RunOverviewAction
    data object OnAnalyticsClick: RunOverviewAction
    data class OnCompareRunClick(val run: RunUi): RunOverviewAction
    data class OnDeleteRunClick(val run: RunUi): RunOverviewAction
    data object OnDiscardRunClick: RunOverviewAction
    data object OnDismissDiscardRunDialogClick: RunOverviewAction
}