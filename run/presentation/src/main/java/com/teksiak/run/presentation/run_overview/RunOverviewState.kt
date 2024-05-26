package com.teksiak.run.presentation.run_overview

import com.teksiak.core.domain.run.Run

data class RunOverviewState(
    val runs: List<Run> = emptyList(),
    val isRunActive: Boolean = false,
    val activeRunDuration: String = "",
    val runToDeleteId: String? = null,
    val showDiscardRunDialog: Boolean = false,
)
