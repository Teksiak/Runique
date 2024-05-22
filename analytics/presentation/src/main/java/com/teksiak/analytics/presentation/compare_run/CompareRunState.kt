package com.teksiak.analytics.presentation.compare_run

import com.teksiak.analytics.presentation.compare_run.model.CompareRunsDataUi
import com.teksiak.core.domain.run.Run

data class CompareRunState(
    val runs: List<Run> = emptyList(),
    val comparedRun: Run? = null,
    val otherRun: Run? = null,
    val compareRunData: CompareRunsDataUi? = null
)
