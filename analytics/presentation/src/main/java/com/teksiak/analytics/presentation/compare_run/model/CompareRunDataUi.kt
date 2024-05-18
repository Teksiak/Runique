package com.teksiak.analytics.presentation.compare_run.model

data class CompareDataUi(
    val data: Pair<String, String>,
    val isEquals: Boolean,
    val isComparedBigger: Boolean
) {
    val first: String
        get() = data.first

    val second: String
        get() = data.second
}

data class CompareRunDataUi(
    val duration: CompareDataUi,
    val distance: CompareDataUi,
    val pace: CompareDataUi,
    val avgSpeed: CompareDataUi,
    val maxSpeed: CompareDataUi,
    val elevation: CompareDataUi
)
