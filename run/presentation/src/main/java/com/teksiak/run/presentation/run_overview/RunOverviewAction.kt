package com.teksiak.run.presentation.run_overview

import com.teksiak.core.domain.run.Run

sealed interface RunOverviewAction {
    data object OnStartRunClick: RunOverviewAction
    data object OnLogoutClick: RunOverviewAction
    data object OnAnalyticsClick: RunOverviewAction
    data class OnCompareRunClick(val run: Run): RunOverviewAction
    data class OnDeleteRunClick(val run: Run?): RunOverviewAction
    data object OnDismissDeleteRunDialog: RunOverviewAction
    data object OnDiscardRunClick: RunOverviewAction
    data object OnDismissDiscardRunDialog: RunOverviewAction
}