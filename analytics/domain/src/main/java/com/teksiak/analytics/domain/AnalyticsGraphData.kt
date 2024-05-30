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
    val runsForSelectedMonth: List<Run>
        get() = runs.filter {
            it.dateTimeUtc.toFormattedMonth() == selectedMonth
        }

    val maxValue: Number
        get() = when (dataType) {
            AnalyticsGraphType.DISTANCE -> runsForSelectedMonth.maxOf { it.distanceMeters }
            AnalyticsGraphType.SPEED -> runsForSelectedMonth.maxOf { it.avgSpeedKmh }
            AnalyticsGraphType.PACE -> runsForSelectedMonth.minOf { it.pace }
        }

    val minValue: Number
        get() = when (dataType) {
            AnalyticsGraphType.DISTANCE -> runsForSelectedMonth.minOf { it.distanceMeters }
            AnalyticsGraphType.SPEED -> runsForSelectedMonth.minOf { it.avgSpeedKmh }
            AnalyticsGraphType.PACE -> runsForSelectedMonth.maxOf { it.pace }
        }

    private val firstDay: Int
        get() = runsForSelectedMonth.firstOrNull()?.dateTimeUtc?.dayOfMonth ?: 1

    private val lastDay: Int
        get() = runsForSelectedMonth.lastOrNull()?.dateTimeUtc?.dayOfMonth ?: 1

    val runByDay: Map<Int, Run?>
        get() = (firstDay..lastDay).associateWith { day ->
            runsForSelectedMonth.find { it.dateTimeUtc.dayOfMonth == day }
        }
}
