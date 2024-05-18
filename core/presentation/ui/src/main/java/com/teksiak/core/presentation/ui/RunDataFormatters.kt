package com.teksiak.core.presentation.ui

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.DurationUnit

fun ZonedDateTime.toFormattedDateTime(): String {
    val dateTimeInLocalTime = this
        .withZoneSameInstant(ZoneId.systemDefault())
    val formattedDateTime = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a")
        .format(dateTimeInLocalTime)
        .replaceFirstChar { it.uppercase(Locale.getDefault()) }

    return formattedDateTime
}

fun Duration.formatted(): String {
    val totalSeconds = inWholeSeconds
    val hours = String.format("%02d", totalSeconds / 3600)
    val minutes = String.format("%02d", totalSeconds % 3600 / 60)
    val seconds = String.format("%02d", totalSeconds % 60)

    return "$hours:$minutes:$seconds"
}

fun Duration.toFormattedTotalDuration(): String {
    val days = toLong(DurationUnit.DAYS)
    val hours = toLong(DurationUnit.HOURS) % 24
    val minutes = toLong(DurationUnit.MINUTES) % 60
    val seconds = toLong(DurationUnit.SECONDS) % 60

    return when {
        days > 0 -> "${days}d ${hours}h ${minutes}m"
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m ${seconds}s"
        else -> "${seconds}s"
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

fun Duration.toFormattedPace(distanceKm: Double): String {
    if(this == Duration.ZERO || distanceKm == 0.0) {
        return "-"
    }

    val secondsPerKm = (inWholeSeconds / distanceKm).roundToInt()
    val avgPaceMinutes = secondsPerKm / 60
    val avgPaceSeconds = String.format("%02d", secondsPerKm % 60)
    return "$avgPaceMinutes:$avgPaceSeconds / km"
}

fun Double.toFormattedKm(): String {
    return "${this.roundToDecimals(2)} km"
}

fun Double.toFormattedKmh(): String {
    return "${this.roundToDecimals(2)} km/h"
}

fun Int.toFormattedMeters(): String {
    return "$this m"
}

private fun Double.roundToDecimals(decimalCount: Int): Double {
    val factor = 10f.pow(decimalCount)
    return round(this * factor) / factor
}