package com.teksiak.analytics.presentation.compare_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CompareRunViewModel: ViewModel() {

    var state by mutableStateOf<CompareRunState?>(
        null
    )

    fun onAction(action: CompareRunAction) {
    }
}