package com.teksiak.wear.run.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.teksiak.wear.run.presentation.R

@Composable
fun TrackerMap(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val mapStyle = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
    }
    val cameraPositionState = rememberCameraPositionState()

    GoogleMap(
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapStyleOptions = mapStyle,
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            scrollGesturesEnabled = false,
        ),
        modifier = modifier
    )
}