package com.teksiak.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teksiak.core.domain.run.RunRepository
import com.teksiak.core.domain.run.RunSyncScheduler
import com.teksiak.core.presentation.ui.formatted
import com.teksiak.run.domain.RunningTracker
import com.teksiak.run.presentation.active_run.service.ActiveRunService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.minutes

class RunOverviewViewModel(
    private val runningTracker: RunningTracker,
    private val runRepository: RunRepository,
    private val runSyncScheduler: RunSyncScheduler,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    var state by mutableStateOf(
        RunOverviewState(
            isRunActive = runningTracker.isTracking.value || ActiveRunService.isServiceActive,
        )
    )
        private set

    init {
        runRepository.getRuns()
            .onEach { runs ->
                state = state.copy(
                    runs = runs
                )
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            runRepository.syncRunsWithRemote()
            runRepository.fetchRuns()
        }

        viewModelScope.launch {
            runSyncScheduler.scheduleSync(
                RunSyncScheduler.SyncType.FetchRuns(30.minutes)
            )
        }

        combine(runningTracker.isTracking, runningTracker.elapsedTime) { isTracking, elapsedTime ->
            isTracking to elapsedTime
        }
            .onEach { (isTracking, elapsedTime) ->
                state = state.copy(
                    isRunActive = isTracking || elapsedTime != ZERO,
                    activeRunDuration = elapsedTime.formatted()
                )
            }
            .launchIn(viewModelScope)

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
        when (action) {
            is RunOverviewAction.OnDeleteRunClick -> {
                state.runToDeleteId?.let { runId ->
                    viewModelScope.launch {
                        runRepository.deleteRun(runId)
                    }
                    state = state.copy(
                        runToDeleteId = null
                    )
                    return@onAction
                }

                state = state.copy(
                    runToDeleteId = action.run?.id
                )
            }

            is RunOverviewAction.OnDismissDeleteRunDialog -> {
                state = state.copy(
                    runToDeleteId = null
                )
            }

            is RunOverviewAction.OnDiscardRunClick -> {
                if (state.showDiscardRunDialog) {
                    runningTracker.finishRun()
                    state = state.copy(
                        showDiscardRunDialog = false,
                        isRunActive = false
                    )
                } else {
                    state = state.copy(
                        showDiscardRunDialog = true
                    )
                }
            }

            is RunOverviewAction.OnDismissDiscardRunDialog -> {
                state = state.copy(
                    showDiscardRunDialog = false
                )
            }

            is RunOverviewAction.OnLogoutClick -> logout()

            else -> Unit
        }
    }

    private fun logout() {
        applicationScope.launch {
            runSyncScheduler.cancelAllSyncs()
            runRepository.logout()
            runRepository.deleteAllRuns()
        }
    }
}