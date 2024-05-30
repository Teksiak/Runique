package com.teksiak.analytics.presentation.dashboard.mapper

import com.teksiak.analytics.domain.AnalyticsData
import com.teksiak.analytics.presentation.dashboard.AnalyticsDashboardState
import com.teksiak.core.presentation.ui.toFormattedKm
import com.teksiak.core.presentation.ui.toFormattedKmh
import com.teksiak.core.presentation.ui.toFormattedTotalDuration

fun AnalyticsData.toAnalyticsDashboardState(): AnalyticsDashboardState {
    return AnalyticsDashboardState(
        totalDistance = (totalDistance / 1000.0).toFormattedKm(),
        totalDuration = totalDuration.toFormattedTotalDuration(),
        maxSpeed = maxSpeed.toFormattedKmh(),
        graphData = graphData
    )
}