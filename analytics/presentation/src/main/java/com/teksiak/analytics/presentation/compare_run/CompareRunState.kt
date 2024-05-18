package com.teksiak.analytics.presentation.compare_run

import com.teksiak.analytics.presentation.compare_run.model.CompareRunDataUi
import com.teksiak.analytics.presentation.compare_run.model.RunUi

data class CompareRunState(
    val runs: List<RunUi> = emptyList(),
    val comparedRun: RunUi? = null,
    val compareRunData: CompareRunDataUi? = null
)
