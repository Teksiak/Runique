package com.teksiak.analytics.domain

import kotlin.time.Duration

data class AnalyticsData(
    val totalDistance: Int = 0,
    val totalDuration: Duration = Duration.ZERO,
    val maxSpeed: Double = 0.0,
    val maxHeartRate: Int? = null,
    val graphData: AnalyticsGraphData = AnalyticsGraphData()
)
