package com.teksiak.run.domain

import com.teksiak.core.domain.location.LocationTimestamp
import kotlin.math.roundToInt
import kotlin.time.DurationUnit

object LocationDataCalculator {

    fun getTotalDistanceMeters(locations: List<List<LocationTimestamp>>): Int {
        return locations
            .sumOf { timestampsPerLine ->
                timestampsPerLine
                    .zipWithNext { location1, location2 ->
                        location1.location.location.distanceTo(location2.location.location)
                    }
                    .sum()
                    .roundToInt()
            }
    }

    fun getMaxSpeedKm(locations: List<List<LocationTimestamp>>): Double {
        return locations.maxOf { locationSet ->
            locationSet.zipWithNext { location1, location2 ->
                val distance = location1.distanceTo(location2)
                val hoursDifference = (location2.durationTimestamp - location1.durationTimestamp).toDouble(DurationUnit.HOURS)

                if(hoursDifference == 0.0) {
                    0.0
                } else {
                    (distance / 1000.0) / hoursDifference
                }
            }.maxOrNull() ?: 0.0
        }
    }

    fun getTotalElevationMeters(locations: List<List<LocationTimestamp>>): Int {
        return locations.sumOf { locationSet ->
            locationSet.zipWithNext { location1, location2 ->
                (location2.altitude - location1.altitude).coerceAtLeast(0.0)
            }.sum().roundToInt()
        }
    }
}