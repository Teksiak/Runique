package com.teksiak.analytics.presentation.compare_run.mapper

import com.teksiak.analytics.domain.CompareRunsData
import com.teksiak.analytics.presentation.compare_run.model.CompareDataUi
import com.teksiak.analytics.presentation.compare_run.model.CompareRunsDataUi
import com.teksiak.core.presentation.ui.toFormattedHeartRate
import com.teksiak.core.presentation.ui.toFormattedKm
import com.teksiak.core.presentation.ui.toFormattedKmh
import com.teksiak.core.presentation.ui.toFormattedMeters
import com.teksiak.core.presentation.ui.toFormattedPace
import com.teksiak.core.presentation.ui.toFormattedTotalDuration

fun CompareRunsData.toCompareRunDataUi(): CompareRunsDataUi {
    val distanceKm = Pair((distance.first!! / 1000.0), (distance.second!! / 1000.0))

    return CompareRunsDataUi(
        duration = CompareDataUi(
            data = Pair(duration.first!!.toFormattedTotalDuration(), duration.second!!.toFormattedTotalDuration()),
            comparison = duration.comparison
        ),
        distance = CompareDataUi(
            data = Pair(distanceKm.first.toFormattedKm(), distanceKm.second.toFormattedKm()),
            comparison = distance.comparison
        ),
        pace = CompareDataUi(
            data = Pair(pace.first!!.toFormattedPace(), pace.second!!.toFormattedPace()),
            comparison = pace.comparison
        ),
        avgSpeed = CompareDataUi(
            data = Pair(avgSpeed.first!!.toFormattedKmh(), avgSpeed.second!!.toFormattedKmh()),
            comparison = avgSpeed.comparison
        ),
        maxSpeed = CompareDataUi(
            data = Pair(maxSpeed.first!!.toFormattedKmh(), maxSpeed.second!!.toFormattedKmh()),
            comparison = maxSpeed.comparison
        ),
        elevation = CompareDataUi(
            data = Pair(elevation.first!!.toFormattedMeters(), elevation.second!!.toFormattedMeters()),
            comparison = elevation.comparison
        ),
        avgHeartRate = CompareDataUi(
            data = Pair(avgHeartRate.first.toFormattedHeartRate(), avgHeartRate.second.toFormattedHeartRate()),
            comparison = avgHeartRate.comparison
        ),
        maxHeartRate = CompareDataUi(
            data = Pair(maxHeartRate.first.toFormattedHeartRate(), maxHeartRate.second.toFormattedHeartRate()),
            comparison = maxHeartRate.comparison
        ),
    )
}