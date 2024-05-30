package com.teksiak.analytics.presentation.dashboard

sealed interface AnalyticsDashboardAction {
    data object OnBackClick: AnalyticsDashboardAction
    data class OnMonthChoose(val month: String): AnalyticsDashboardAction
}