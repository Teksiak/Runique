package com.teksiak.run.presentation.run_overview

import com.teksiak.run.presentation.run_overview.model.RunUi

data class RunOverviewState(
    val runs: List<RunUi> = emptyList(),
    val isRunActive: Boolean = false,
    val activeRunDuration: String = "",
    val isDiscardRunDialogShown: Boolean = false
)
