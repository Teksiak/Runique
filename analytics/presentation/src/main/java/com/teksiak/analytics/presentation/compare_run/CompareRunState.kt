package com.teksiak.analytics.presentation.compare_run

import com.teksiak.analytics.presentation.compare_run.model.CompareRunDataUi
import com.teksiak.core.domain.run.Run

data class CompareRunState(
    val comparingRun: Run,
    val otherRun: Run,
    val compareRunData: CompareRunDataUi
)
