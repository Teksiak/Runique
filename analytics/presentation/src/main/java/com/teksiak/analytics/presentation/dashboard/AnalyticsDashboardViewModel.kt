package com.teksiak.analytics.presentation.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teksiak.analytics.domain.AnalyticsRepository
import com.teksiak.analytics.presentation.dashboard.mapper.toAnalyticsDashboardState
import kotlinx.coroutines.launch

class AnalyticsDashboardViewModel(
    private val analyticsRepository: AnalyticsRepository,
): ViewModel() {

    var state by mutableStateOf<AnalyticsDashboardState?>(null)
        private set

    init {
        viewModelScope.launch {
            state = analyticsRepository.getAnalyticsData().toAnalyticsDashboardState()
        }
    }

    fun onAction(action: AnalyticsDashboardAction) {
        when(action) {
            is AnalyticsDashboardAction.OnMonthChoose -> { }
            is AnalyticsDashboardAction.OnDayChoose -> {
                state?.let {
                    state = it.copy(
                        selectedDay = action.day
                    )
                }
            }
            else -> Unit
        }
    }

}