package com.teksiak.analytics.presentation

sealed interface AnalyticsAction {
    data object OnBackClick: AnalyticsAction
}