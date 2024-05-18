package com.teksiak.analytics.domain.mapper

import com.teksiak.analytics.domain.CompareData
import com.teksiak.analytics.domain.CompareRunData
import com.teksiak.core.domain.run.Run
import kotlin.time.Duration.Companion.seconds

fun Pair<Run, Run>.toCompareRunData(): CompareRunData {
    return CompareRunData(
        duration = CompareData(Pair(first.duration, second.duration)),
        distance = CompareData(Pair(first.distanceMeters, second.distanceMeters)),
        pace = CompareData(Pair(first.pace.seconds, second.pace.seconds)),
        avgSpeed = CompareData(Pair(first.avgSpeedKmh, second.avgSpeedKmh)),
        maxSpeed = CompareData(Pair(first.maxSpeedKmh, second.maxSpeedKmh)),
        elevation = CompareData(Pair(first.totalElevationMeters, second.totalElevationMeters))
    )
}