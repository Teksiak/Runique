package com.teksiak.core.presentation.ui.mapper

import com.teksiak.core.domain.run.Run
import com.teksiak.core.presentation.ui.formatted
import com.teksiak.core.presentation.ui.model.RunUi
import com.teksiak.core.presentation.ui.toFormattedDateTime
import com.teksiak.core.presentation.ui.toFormattedHeartRate
import com.teksiak.core.presentation.ui.toFormattedKm
import com.teksiak.core.presentation.ui.toFormattedKmh
import com.teksiak.core.presentation.ui.toFormattedMeters
import com.teksiak.core.presentation.ui.toFormattedPace

fun Run.toRunUi(): RunUi {
    val distanceKm = distanceMeters / 1000.0

    return RunUi(
        id = id!!,
        duration = duration.formatted(),
        dateTime = dateTimeUtc.toFormattedDateTime(),
        distance = distanceKm.toFormattedKm(),
        avgSpeed = avgSpeedKmh.toFormattedKmh(),
        maxSpeed = maxSpeedKmh.toFormattedKmh(),
        avgHeartRate = avgHeartRate.toFormattedHeartRate(),
        maxHeartRate = maxHeartRate.toFormattedHeartRate(),
        pace = duration.toFormattedPace(distanceKm),
        totalElevation = totalElevationMeters.toFormattedMeters(),
        mapPictureUrl = mapPictureUrl,
    )
}