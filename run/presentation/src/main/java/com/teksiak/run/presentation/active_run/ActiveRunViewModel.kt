package com.teksiak.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class ActiveRunViewModel: ViewModel() {

    var state by mutableStateOf(ActiveRunState())
        private set

    private val _eventChannel = Channel<ActiveRunEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val _hasLocationPermission = MutableStateFlow(false)

    fun onAction(action: ActiveRunAction) {
        when(action) {
            ActiveRunAction.OnFinishRunClick -> {}
            ActiveRunAction.OnResumeRunClick -> {}
            ActiveRunAction.OnToggleRunClick -> {}
            is ActiveRunAction.SubmitLocationPermissionInfo -> {
                _hasLocationPermission.update { action.acceptedLocationPermission }
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
            else -> Unit
        }
    }
}