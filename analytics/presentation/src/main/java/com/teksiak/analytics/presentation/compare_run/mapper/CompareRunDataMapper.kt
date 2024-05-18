package com.teksiak.analytics.presentation.compare_run.mapper

import com.teksiak.analytics.domain.CompareRunData
import com.teksiak.analytics.presentation.compare_run.model.CompareDataUi
import com.teksiak.analytics.presentation.compare_run.model.CompareRunDataUi
import com.teksiak.core.presentation.ui.toFormattedKm
import com.teksiak.core.presentation.ui.toFormattedKmh
import com.teksiak.core.presentation.ui.toFormattedMeters
import com.teksiak.core.presentation.ui.toFormattedPace
import com.teksiak.core.presentation.ui.toFormattedTotalDuration

fun CompareRunData.toCompareRunDataUi(): CompareRunDataUi {
    val distanceKm = Pair((distance.first / 1000.0), (distance.second / 1000.0))

    return CompareRunDataUi(
        duration = CompareDataUi(
            data = Pair(duration.first.toFormattedTotalDuration(), duration.second.toFormattedTotalDuration()),
            isEquals = duration.isEquals,
            isComparedBigger = duration.isComparedBigger
        ),
        distance = CompareDataUi(
            data = Pair(distanceKm.first.toFormattedKm(), distanceKm.second.toFormattedKm()),
            isEquals = distance.isEquals,
            isComparedBigger = distance.isComparedBigger
        ),
        pace = CompareDataUi(
            data = Pair(pace.first.toFormattedPace(distanceKm.first), pace.second.toFormattedPace(distanceKm.second)),
            isEquals = pace.isEquals,
            isComparedBigger = pace.isComparedBigger
        ),
        avgSpeed = CompareDataUi(
            data = Pair(avgSpeed.first.toFormattedKmh(), avgSpeed.second.toFormattedKmh()),
            isEquals = avgSpeed.isEquals,
            isComparedBigger = avgSpeed.isComparedBigger
        ),
        maxSpeed = CompareDataUi(
            data = Pair(maxSpeed.first.toFormattedKmh(), maxSpeed.second.toFormattedKmh()),
            isEquals = maxSpeed.isEquals,
            isComparedBigger = maxSpeed.isComparedBigger
        ),
        elevation = CompareDataUi(
            data = Pair(elevation.first.toFormattedMeters(), elevation.second.toFormattedMeters()),
            isEquals = elevation.isEquals,
            isComparedBigger = elevation.isComparedBigger
        )
    )
}