package com.teksiak.core.domain.location

import kotlin.time.Duration

data class LocationTimestamp(
    val location: LocationWithAltitude,
    val durationTimestamp: Duration
) {
    val lat: Double
        get() = location.lat
    val long: Double
        get() = location.long

    val altitude: Double
        get() = location.altitude

    fun distanceTo(other: LocationTimestamp): Float {
        return location.distanceTo(other.location)
    }
}
