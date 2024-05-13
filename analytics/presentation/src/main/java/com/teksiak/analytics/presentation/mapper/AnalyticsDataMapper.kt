package com.teksiak.analytics.presentation.mapper

import com.teksiak.analytics.domain.AnalyticsData
import com.teksiak.analytics.presentation.AnalyticsDashboardState
import com.teksiak.core.presentation.ui.formatted
import com.teksiak.core.presentation.ui.toFormattedKm
import com.teksiak.core.presentation.ui.toFormattedKmh
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

fun Duration.toFormattedTotalDuration(): String {
    val days = toLong(DurationUnit.DAYS)
    val hours = toLong(DurationUnit.HOURS) % 24
    val minutes = toLong(DurationUnit.MINUTES) % 60

    return when {
        days > 0 -> "${days}d ${hours}h ${minutes}m"
        hours > 0 -> "${hours}h ${minutes}m"
        else -> "${minutes}m"
    }
}

fun Duration.toFormattedPace(): String {
    if (this == Duration.ZERO) {
        return "-"
    }

    val totalSeconds = inWholeSeconds
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "${minutes}:${String.format("%02d", seconds)} / km"
}

fun AnalyticsData.toAnalyticsDashboardState(): AnalyticsDashboardState {
    return AnalyticsDashboardState(
        totalDistance = (totalDistance / 1000.0).toFormattedKm(),
        totalDuration = totalDuration.toFormattedTotalDuration(),
        maxSpeed = maxSpeed.toFormattedKmh(),
        avgDistance = (avgDistancePerRun / 1000.0).toFormattedKmh(),
        avgPace = avgPacePerRun.seconds.toFormattedPace()
    )
}