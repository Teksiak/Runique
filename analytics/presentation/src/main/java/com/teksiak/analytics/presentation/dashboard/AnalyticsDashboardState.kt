package com.teksiak.analytics.presentation.dashboard

import com.teksiak.analytics.domain.AnalyticsGraphData

data class AnalyticsDashboardState(
    val totalDistance: String = "-",
    val totalDuration: String = "-",
    val maxSpeed: String = "-",
    val graphData: AnalyticsGraphData = AnalyticsGraphData(),
    val selectedDay: Int? = null
)
