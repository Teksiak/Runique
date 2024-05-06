package com.teksiak.run.presentation.active_run.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Polyline
import com.teksiak.core.domain.location.LocationTimestamp

@Composable
fun RuniquePolylines(
    locations: List<List<LocationTimestamp>>
) {
    val polylines = remember(locations) {
        locations.map {
            it.zipWithNext { timestamp1, timestamp2 ->
                PolylineUi(
                    location1 = timestamp1.location.location,
                    location2 = timestamp2.location.location,
                    color = PolylineColorCalculator.locationsToColor(timestamp1, timestamp2)
                )
            }
        }
    }

    polylines.forEach { polyline ->
        polyline.forEach { polyLineUi ->
            Polyline(
                points = listOf(
                    LatLng(polyLineUi.location1.lat, polyLineUi.location1.long),
                    LatLng(polyLineUi.location2.lat, polyLineUi.location2.long)
                ),
                color = polyLineUi.color,
                jointType = JointType.BEVEL
            )
        }

    }
}