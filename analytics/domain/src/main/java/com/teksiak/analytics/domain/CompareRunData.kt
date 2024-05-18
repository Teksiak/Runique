package com.teksiak.analytics.domain

import kotlin.time.Duration

enum class DataComparison {
    EQUALS,
    FIRST_BIGGER,
    SECOND_BIGGER
}

data class CompareData<T: Comparable<T>>(
    val data: Pair<T, T>
) {
    val first: T
        get() = data.first

    val second: T
        get() = data.second

    val comparison: DataComparison
        get() = when {
            data.first == data.second -> DataComparison.EQUALS
            data.first > data.second -> DataComparison.FIRST_BIGGER
            else -> DataComparison.SECOND_BIGGER
        }
}

data class CompareRunData(
    val duration: CompareData<Duration>,
    val distance: CompareData<Int>,
    val pace: CompareData<Duration>,
    val avgSpeed: CompareData<Double>,
    val maxSpeed: CompareData<Double>,
    val elevation: CompareData<Int>
)
