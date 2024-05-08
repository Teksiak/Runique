package com.teksiak.run.presentation.run_overview.mappers

import androidx.compose.ui.text.capitalize
import com.teksiak.core.domain.run.Run
import com.teksiak.core.presentation.ui.formatted
import com.teksiak.core.presentation.ui.toFormattedKm
import com.teksiak.core.presentation.ui.toFormattedKmh
import com.teksiak.core.presentation.ui.toFormattedMeters
import com.teksiak.core.presentation.ui.toFormattedPace
import com.teksiak.run.presentation.run_overview.model.RunUi
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Run.toRunUi(): RunUi {
    val dateTimeInLocalTime = dateTimeUtc
        .withZoneSameInstant(ZoneId.systemDefault())
    val formattedDateTime = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a")
        .format(dateTimeInLocalTime)
        .replaceFirstChar { it.uppercase(Locale.getDefault()) }

    val distanceKm = distanceMeters / 1000.0

    return RunUi(
        id = id!!,
        duration = duration.formatted(),
        dateTime = formattedDateTime,
        distance = distanceKm.toFormattedKm(),
        avgSpeed = avgSpeedKmh.toFormattedKmh(),
        maxSpeed = maxSpeedKmh.toFormattedKmh(),
        pace = duration.toFormattedPace(distanceKm),
        totalElevation = totalElevationMeters.toFormattedMeters(),
        mapPictureUrl = mapPictureUrl
    )
}