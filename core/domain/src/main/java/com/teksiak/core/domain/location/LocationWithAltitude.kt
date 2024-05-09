package com.teksiak.core.domain.location

data class LocationWithAltitude(
    val location: Location,
    val altitude: Double
) {
    val lat: Double
        get() = location.lat
    val long: Double
        get() = location.long

    fun distanceTo(other: LocationWithAltitude): Float {
        return location.distanceTo(other.location)
    }
}
