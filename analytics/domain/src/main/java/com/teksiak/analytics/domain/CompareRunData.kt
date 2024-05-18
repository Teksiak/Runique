package com.teksiak.analytics.domain

import kotlin.time.Duration

data class CompareData<T: Comparable<T>>(
    val data: Pair<T, T>
) {
    val first: T
        get() = data.first

    val second: T
        get() = data.second

    val isEquals: Boolean
        get() = data.first == data.second

    val isComparedBigger: Boolean
        get() = data.first < data.second
}

data class CompareRunData(
    val duration: CompareData<Duration>,
    val distance: CompareData<Int>,
    val pace: CompareData<Duration>,
    val avgSpeed: CompareData<Double>,
    val maxSpeed: CompareData<Double>,
    val elevation: CompareData<Int>
)
