package com.teksiak.run.presentation.active_run

import com.teksiak.core.domain.location.Location
import com.teksiak.run.domain.RunData
import kotlin.time.Duration

data class ActiveRunState(
    val elapsedTime: Duration = Duration.ZERO,
    val runData: RunData = RunData(),
    val shouldTrack: Boolean = false,
    val hasStartedRunning: Boolean = false,
    val currentLocation: Location? = null,
    val isRunFinished: Boolean = false,
    val isSavingRun: Boolean = false,
    val showLocationPermissionRationale: Boolean = false,
    val showNotificationPermissionRationale: Boolean = false
)
