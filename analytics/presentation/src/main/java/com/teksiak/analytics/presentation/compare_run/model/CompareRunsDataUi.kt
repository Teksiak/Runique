package com.teksiak.analytics.presentation.compare_run.model

import com.teksiak.analytics.domain.DataComparison

data class CompareDataUi(
    val data: Pair<String, String>,
    val comparison: DataComparison
)

data class CompareRunsDataUi(
    val duration: CompareDataUi,
    val distance: CompareDataUi,
    val pace: CompareDataUi,
    val avgSpeed: CompareDataUi,
    val maxSpeed: CompareDataUi,
    val elevation: CompareDataUi
)
