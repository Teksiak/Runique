package com.teksiak.analytics.domain.mapper

import com.teksiak.analytics.domain.CompareData
import com.teksiak.analytics.domain.CompareRunsData
import com.teksiak.core.domain.run.Run
import kotlin.time.Duration.Companion.seconds

fun Pair<Run, Run>.toCompareRunsData(): CompareRunsData {
    return CompareRunsData(
        duration = CompareData(first.duration, second.duration),
        distance = CompareData(first.distanceMeters, second.distanceMeters),
        pace = CompareData(first.pace.seconds, second.pace.seconds),
        avgSpeed = CompareData(first.avgSpeedKmh, second.avgSpeedKmh),
        maxSpeed = CompareData(first.maxSpeedKmh, second.maxSpeedKmh),
        elevation = CompareData(first.totalElevationMeters, second.totalElevationMeters)
    )
}