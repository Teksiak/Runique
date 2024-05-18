package com.teksiak.analytics.presentation.dashboard.mapper

import com.teksiak.analytics.domain.AnalyticsData
import com.teksiak.analytics.presentation.dashboard.AnalyticsDashboardState
import com.teksiak.core.presentation.ui.toFormattedKm
import com.teksiak.core.presentation.ui.toFormattedKmh
import com.teksiak.core.presentation.ui.toFormattedPace
import com.teksiak.core.presentation.ui.toFormattedTotalDuration
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

fun AnalyticsData.toAnalyticsDashboardState(): AnalyticsDashboardState {
    return AnalyticsDashboardState(
        totalDistance = (totalDistance / 1000.0).toFormattedKm(),
        totalDuration = totalDuration.toFormattedTotalDuration(),
        maxSpeed = maxSpeed.toFormattedKmh(),
        avgDistance = (avgDistancePerRun / 1000.0).toFormattedKmh(),
        avgPace = avgPacePerRun.seconds.toFormattedPace()
    )
}