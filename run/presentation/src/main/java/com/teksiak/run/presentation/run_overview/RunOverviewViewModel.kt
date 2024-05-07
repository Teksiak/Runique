package com.teksiak.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teksiak.run.domain.RunningTracker
import com.teksiak.run.presentation.active_run.service.ActiveRunService
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RunOverviewViewModel(
    private val runningTracker: RunningTracker
): ViewModel() {

    var state by mutableStateOf(RunOverviewState(
        isRunActive = ActiveRunService.isServiceActive
    ))
        private set

    init {
        runningTracker.isTracking
            .onEach { isTracking ->
                state = state.copy(
                    isRunActive = ActiveRunService.isServiceActive || isTracking
                )
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: RunOverviewAction) {
        when(action) {
            is RunOverviewAction.OnCheckRunStatus -> {
                state = state.copy(
                    isRunActive = ActiveRunService.isServiceActive
                )
            }
            else -> Unit
        }
    }
}