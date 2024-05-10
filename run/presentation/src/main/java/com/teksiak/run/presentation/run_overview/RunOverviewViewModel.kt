package com.teksiak.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teksiak.core.domain.run.RunRepository
import com.teksiak.core.presentation.ui.formatted
import com.teksiak.run.domain.RunningTracker
import com.teksiak.run.presentation.run_overview.mappers.toRunUi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RunOverviewViewModel(
    private val runningTracker: RunningTracker,
    private val runRepository: RunRepository
): ViewModel() {

    var state by mutableStateOf(RunOverviewState(
        isRunActive = runningTracker.isTracking.value,
    ))
        private set

    init {
        runRepository.getRuns()
            .onEach { runs ->
                val runsUi = runs.map { it.toRunUi() }
                state = state.copy(
                    runs = runsUi
                )
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            runRepository.fetchRuns()
        }

        runningTracker.isTracking
            .onEach { isTracking ->
                state = state.copy(
                    isRunActive = isTracking
                )
            }
            .launchIn(viewModelScope)

        runningTracker.elapsedTime
            .onEach {
                state = state.copy(
                    activeRunDuration = it.formatted()
                )
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: RunOverviewAction) {
        when(action) {
            is RunOverviewAction.OnDeleteRunClick -> {
                viewModelScope.launch {
                    runRepository.deleteRun(action.run.id)
                }
            }
            is RunOverviewAction.OnDiscardRunClick -> {
                if(state.isDiscardRunDialogShown) {
                    runningTracker.finishRun()
                    state = state.copy(
                        isDiscardRunDialogShown = false
                    )
                } else {
                    state = state.copy(
                        isDiscardRunDialogShown = true
                    )
                }
            }
            is RunOverviewAction.OnDismissDiscardRunDialogClick -> {
                state = state.copy(
                    isDiscardRunDialogShown = false
                )
            }
            else -> Unit
        }
    }
}