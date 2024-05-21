package com.teksiak.analytics.domain

import kotlin.time.Duration

enum class DataComparison {
    EQUALS,
    FIRST_BIGGER,
    SECOND_BIGGER
}

data class CompareData<T: Comparable<T>>(
    val first: T,
    val second: T
) {

    val comparison: DataComparison
        get() = when {
            first == second -> DataComparison.EQUALS
            first > second -> DataComparison.FIRST_BIGGER
            else -> DataComparison.SECOND_BIGGER
        }
}

data class CompareRunsData(
    val duration: CompareData<Duration>,
    val distance: CompareData<Int>,
    val pace: CompareData<Duration>,
    val avgSpeed: CompareData<Double>,
    val maxSpeed: CompareData<Double>,
    val elevation: CompareData<Int>
)
