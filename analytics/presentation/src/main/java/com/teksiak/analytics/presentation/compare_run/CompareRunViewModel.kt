package com.teksiak.analytics.presentation.compare_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teksiak.analytics.domain.mapper.toCompareRunsData
import com.teksiak.analytics.presentation.compare_run.mapper.toCompareRunDataUi
import com.teksiak.core.domain.run.Run
import com.teksiak.core.domain.run.RunRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class CompareRunViewModel(
    savedStateHandle: SavedStateHandle,
    runRepository: RunRepository
): ViewModel() {

    private val runId = savedStateHandle.get<String>("runId")!!

    var state by mutableStateOf( CompareRunState() )
        private set

    private val runsData = MutableStateFlow<List<Run>>(emptyList())

    private val comparedRuns = MutableStateFlow<Pair<Run?, Run?>>(
        Pair(null, null)
    )

    init {
        runRepository.getRuns()
            .onEach { runs ->
                val comparedRun = runs.find { it.id == runId }
                runsData.value = runs
                comparedRuns.update { comparedRun to it.second }
            }
            .launchIn(viewModelScope)

        runsData.onEach { runs ->
            state = state.copy(
                runs = runs
                    .filter { it.id != runId }
            )
        }.launchIn(viewModelScope)

        comparedRuns.onEach {
            state = state.copy(
                comparedRun = it.first,
                otherRun = it.second
            )
            it.first?.let { comparedRun ->
                it.second?.let { otherRun ->
                    state = state.copy(
                        compareRunData = (otherRun to comparedRun).toCompareRunsData().toCompareRunDataUi()
                    )
                    return@onEach
                }
            }
            state = state.copy(
                compareRunData = null
            )
        }.launchIn(viewModelScope)
    }

    fun onAction(action: CompareRunAction) {
        when(action) {
            is CompareRunAction.OnOtherRunChoose -> {
                val otherRun = runsData.value.find { it.id == action.runId }
                comparedRuns.update { it.first to otherRun }
            }
            is CompareRunAction.OnBackClick -> {
                comparedRuns.update { it.first to null }
            }
        }
    }
}