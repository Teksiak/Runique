package com.teksiak.run.presentation.run_overview.mappers

import android.content.Context
import android.location.Geocoder
import com.teksiak.core.domain.run.Run
import com.teksiak.core.presentation.ui.formatted
import com.teksiak.core.presentation.ui.toFormattedDateTime
import com.teksiak.core.presentation.ui.toFormattedKm
import com.teksiak.core.presentation.ui.toFormattedKmh
import com.teksiak.core.presentation.ui.toFormattedMeters
import com.teksiak.core.presentation.ui.toFormattedPace
import com.teksiak.run.presentation.run_overview.model.RunUi

fun Run.toRunUi(): RunUi {
    val distanceKm = distanceMeters / 1000.0

    return RunUi(
        id = id!!,
        duration = duration.formatted(),
        dateTime = dateTimeUtc.toFormattedDateTime(),
        distance = distanceKm.toFormattedKm(),
        avgSpeed = avgSpeedKmh.toFormattedKmh(),
        maxSpeed = maxSpeedKmh.toFormattedKmh(),
        pace = duration.toFormattedPace(distanceKm),
        totalElevation = totalElevationMeters.toFormattedMeters(),
        mapPictureUrl = mapPictureUrl,
    )
}