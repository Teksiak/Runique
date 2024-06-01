package com.teksiak.analytics.presentation.dashboard

import com.teksiak.analytics.domain.AnalyticsGraphType

sealed interface AnalyticsDashboardAction {
    data object OnBackClick: AnalyticsDashboardAction
    data class OnGraphTypeSelect(val type: AnalyticsGraphType): AnalyticsDashboardAction
    data class OnDaySelect(val day: Int): AnalyticsDashboardAction
    data class OnMonthSelect(val month: String): AnalyticsDashboardAction
}