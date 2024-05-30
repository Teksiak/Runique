package com.teksiak.analytics.domain

import com.teksiak.analytics.domain.util.toFormattedMonth
import com.teksiak.core.domain.run.Run

enum class AnalyticsGraphType(val title: String) {
    DISTANCE("Distance"),
    SPEED("Avg. speed"),
    PACE("Pace")
}

data class AnalyticsGraphData(
    val runs: List<Run> = emptyList(),
    val distinctMonths: List<String> = runs.map { it.dateTimeUtc.toFormattedMonth() }.distinct(),
    val selectedMonth: String? = distinctMonths.firstOrNull(),
    val dataType: AnalyticsGraphType = AnalyticsGraphType.DISTANCE,
) {
    private val runsForSelectedMonth: List<Run>
        get() = runs.filter {
            it.dateTimeUtc.toFormattedMonth() == selectedMonth
        }

    val maxValue: Number
        get() = when (dataType) {
            AnalyticsGraphType.DISTANCE -> runsForSelectedMonth.maxOf { it.distanceMeters }
            AnalyticsGraphType.SPEED -> runsForSelectedMonth.maxOf { it.avgSpeedKmh }
            AnalyticsGraphType.PACE -> runsForSelectedMonth.minOf { it.pace }
        }

    private val minValue: Number
        get() = when (dataType) {
            AnalyticsGraphType.DISTANCE -> runsForSelectedMonth.minOf { it.distanceMeters }
            AnalyticsGraphType.SPEED -> runsForSelectedMonth.minOf { it.avgSpeedKmh }
            AnalyticsGraphType.PACE -> runsForSelectedMonth.maxOf { it.pace }
        }

    val valuesRange: Float
        get() = maxValue.toFloat() - minValue.toFloat()

    val firstDay: Int
        get() = runsForSelectedMonth.firstOrNull()?.dateTimeUtc?.dayOfMonth ?: 1

    val lastDay: Int
        get() = runsForSelectedMonth.lastOrNull()?.dateTimeUtc?.dayOfMonth ?: 1

    val daysRange: Float
        get() = (lastDay - firstDay).toFloat()

    val days: List<Int>
        get() = (firstDay..lastDay step if(daysRange > 15) 2 else 1).toList()

    val runByDay: Map<Int, Run>
        get() = runsForSelectedMonth.run {
            when (dataType) {
                AnalyticsGraphType.DISTANCE -> sortedByDescending { it.distanceMeters }
                AnalyticsGraphType.SPEED -> sortedByDescending { it.avgSpeedKmh }
                AnalyticsGraphType.PACE -> sortedByDescending { it.pace }
            }
        }
            .distinctBy {
                it.dateTimeUtc.dayOfMonth
            }
            .sortedBy {
                it.dateTimeUtc.dayOfMonth
            }
            .associateBy { it.dateTimeUtc.dayOfMonth }

    val valueByDay: Map<Int, Number>
        get() = runByDay.mapValues { (_, run) ->
            when (dataType) {
                AnalyticsGraphType.DISTANCE -> run.distanceMeters
                AnalyticsGraphType.SPEED -> run.avgSpeedKmh
                AnalyticsGraphType.PACE -> run.pace
            }
        }
}
