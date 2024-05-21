package com.teksiak.analytics.presentation.compare_run

import com.teksiak.analytics.presentation.compare_run.model.CompareRunsDataUi
import com.teksiak.analytics.presentation.compare_run.model.RunUi

data class CompareRunState(
    val runs: List<RunUi> = emptyList(),
    val comparedRun: RunUi? = null,
    val otherRun: RunUi? = null,
    val compareRunData: CompareRunsDataUi? = null
)
