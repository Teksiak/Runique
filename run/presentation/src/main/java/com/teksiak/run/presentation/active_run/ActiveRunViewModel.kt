package com.teksiak.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teksiak.core.connectivity.domain.messaging.MessagingAction
import com.teksiak.core.domain.location.Location
import com.teksiak.core.domain.run.Run
import com.teksiak.core.domain.run.RunRepository
import com.teksiak.core.domain.util.Result
import com.teksiak.core.notification.ActiveRunService
import com.teksiak.core.presentation.ui.asUiText
import com.teksiak.run.domain.LocationDataCalculator
import com.teksiak.run.domain.RunningTracker
import com.teksiak.run.domain.WatchConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.roundToInt

class ActiveRunViewModel(
    private val runningTracker: RunningTracker,
    private val runRepository: RunRepository,
    private val watchConnector: WatchConnector,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    var state by mutableStateOf(
        ActiveRunState(
            shouldTrack = ActiveRunService.isServiceActive.value && runningTracker.isTracking.value,
            hasStartedRunning = ActiveRunService.isServiceActive.value,
        )
    )
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val events = eventChannel.receiveAsFlow()

    private val hasLocationPermission = MutableStateFlow(false)

    init {
        watchConnector
            .connectedDevice
            .filterNotNull()
            .onEach {
                Timber.d("New device detected: ${it.displayName}")
            }
            .launchIn(viewModelScope)

        hasLocationPermission
            .onEach { hasPermission ->
                if (hasPermission) {
                    runningTracker.startObservingLocation()
                } else {
                    runningTracker.stopObservingLocation()
                }
            }
            .launchIn(viewModelScope)


        runningTracker
            .isTracking
            .onEach { isTracking ->
                state = state.copy(
                    shouldTrack = isTracking
                )
            }
            .launchIn(viewModelScope)

        runningTracker
            .currentLocation
            .onEach {
                state = state.copy(currentLocation = it?.location)
            }
            .launchIn(viewModelScope)

        runningTracker
            .runData
            .onEach {
                state = state.copy(runData = it)
            }
            .launchIn(viewModelScope)

        runningTracker
            .elapsedTime
            .onEach {
                state = state.copy(elapsedTime = it)
            }
            .launchIn(viewModelScope)

        listenToWatchActions()
    }

    fun onAction(action: ActiveRunAction, triggeredOnWatch: Boolean = false) {
        if(!triggeredOnWatch) {
            val messagingAction = when(action) {
                ActiveRunAction.OnFinishRunClick -> MessagingAction.Finish
                ActiveRunAction.OnResumeRunClick -> MessagingAction.StartOrResume
                ActiveRunAction.OnToggleRunClick -> {
                    if(state.hasStartedRunning) {
                        MessagingAction.Pause
                    } else {
                        MessagingAction.StartOrResume
                    }
                }
                else -> null
            }

            messagingAction?.let {
                viewModelScope.launch {
                    watchConnector.sendActionToWatch(it)
                }
            }
        }
        when (action) {
            ActiveRunAction.OnFinishRunClick -> {
                if (state.runData.distanceMeters == 0) {
                    state = state.copy(isSavingRun = false)
                    return
                }

                state = state.copy(
                    isRunFinished = true,
                    isSavingRun = true
                )
            }

            ActiveRunAction.OnResumeRunClick -> {
                runningTracker.setIsTracking(true)
            }

            ActiveRunAction.OnToggleRunClick -> {
                runningTracker.setIsTracking(!state.shouldTrack)
                state = state.copy(
                    hasStartedRunning = true
                )
            }

            ActiveRunAction.OnDiscardRunClick -> {
                if (state.hasStartedRunning) {
                    state = state.copy(
                        showDiscardRunDialog = true
                    )
                }
            }

            ActiveRunAction.OnDiscardRunConfirm -> {
                state = state.copy(
                    hasStartedRunning = false,
                    showDiscardRunDialog = false,
                )
                runningTracker.finishRun()
            }

            ActiveRunAction.DismissDiscardRunDialog -> {
                state = state.copy(
                    showDiscardRunDialog = false
                )
            }

            is ActiveRunAction.SubmitLocationPermissionInfo -> {
                hasLocationPermission.update { action.acceptedLocationPermission }
                state = state.copy(
                    showLocationPermissionRationale = action.showLocationPermissionRationale
                )
            }

            is ActiveRunAction.SubmitNotificationPermissionInfo -> {
                state = state.copy(
                    showNotificationPermissionRationale = action.showNotificationPermissionRationale
                )
            }

            is ActiveRunAction.DismissRationaleDialog -> {
                state = state.copy(
                    showLocationPermissionRationale = false,
                    showNotificationPermissionRationale = false
                )
            }

            is ActiveRunAction.OnRunProcessed -> {
                finishRun(action.mapPictureBytes)
            }

            else -> Unit
        }
    }

    private fun finishRun(mapPictureBytes: ByteArray) {
        val locations = state.runData.locations


        viewModelScope.launch {
            val run = Run(
                id = null,
                duration = state.elapsedTime,
                dateTimeUtc = ZonedDateTime.now()
                    .withZoneSameInstant(ZoneId.of("UTC")),
                distanceMeters = state.runData.distanceMeters,
                location = state.currentLocation ?: Location(0.0, 0.0),
                maxSpeedKmh = LocationDataCalculator.getMaxSpeedKm(locations),
                totalElevationMeters = LocationDataCalculator.getTotalElevationMeters(locations),
                mapPictureUrl = null,
                avgHeartRate = if (state.runData.heartRates.isEmpty()) {
                    null
                } else {
                    state.runData.heartRates.average().roundToInt()
                },
                maxHeartRate = state.runData.heartRates.maxOrNull()
            )

            runningTracker.finishRun()

            when (val result = runRepository.upsertRun(run, mapPictureBytes)) {
                is Result.Error -> {
                    eventChannel.send(ActiveRunEvent.Error(result.error.asUiText()))
                }

                is Result.Success -> {
                    state = state.copy(
                        hasStartedRunning = false,
                        shouldTrack = false,
                    )
                    eventChannel.send(ActiveRunEvent.RunSaved)
                }
            }

            state = state.copy(isSavingRun = false)
        }
    }

    private fun listenToWatchActions() {
        watchConnector
            .messagingActions
            .onEach { action ->
                Timber.d("Received action from watch: $action")
                when (action) {
                    MessagingAction.ConnectionRequest -> {
                        if (state.hasStartedRunning && state.shouldTrack) {
                            watchConnector.sendActionToWatch(MessagingAction.StartOrResume)
                        }
                    }

                    MessagingAction.Finish -> {
                        onAction(
                            action = ActiveRunAction.OnFinishRunClick,
                            triggeredOnWatch = true
                        )
                    }

                    MessagingAction.Pause -> {
                        if (state.hasStartedRunning) {
                            onAction(
                                action = ActiveRunAction.OnToggleRunClick,
                                triggeredOnWatch = true
                            )
                        }
                    }

                    MessagingAction.StartOrResume -> {
                        onAction(
                            action = if (state.hasStartedRunning) ActiveRunAction.OnResumeRunClick
                            else ActiveRunAction.OnToggleRunClick,
                            triggeredOnWatch = true
                        )
                    }

                    else -> Unit
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        if (!ActiveRunService.isServiceActive.value) {
            applicationScope.launch {
                watchConnector.sendActionToWatch(MessagingAction.Untrackable)
            }
            runningTracker.stopObservingLocation()
        }
    }
}