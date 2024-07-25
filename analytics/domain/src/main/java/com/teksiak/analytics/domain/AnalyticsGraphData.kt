package com.teksiak.analytics.domain

import com.teksiak.analytics.domain.util.toFormattedMonth
import com.teksiak.core.domain.run.Run

enum class AnalyticsGraphType(val title: String) {
    SPEED("Avg. speed"),
    PACE("Pace"),
    HEART("Avg. heart rate"),
}

data class AnalyticsGraphData(
    val runs: List<Run> = emptyList(),
    val distinctMonths: List<String> = runs.map { it.dateTimeUtc.toFormattedMonth() }.distinct(),
    val selectedMonth: String? = distinctMonths.lastOrNull(),
    val dataType: AnalyticsGraphType = AnalyticsGraphType.SPEED,
) {
    private val runsForSelectedMonth: List<Run>
        get() = runs
            .filter {
                it.dateTimeUtc.toFormattedMonth() == selectedMonth
            }
            .filter {
                if(dataType == AnalyticsGraphType.HEART) it.avgHeartRate != null else true
            }

    val maxValue: Number
        get() = when (dataType) {
            AnalyticsGraphType.SPEED -> runsForSelectedMonth.maxOf { it.avgSpeedKmh }
            AnalyticsGraphType.PACE -> runsForSelectedMonth.minOf { it.pace }
            AnalyticsGraphType.HEART -> runsForSelectedMonth.maxOf { it.avgHeartRate!! }
        }

    private val minValue: Number
        get() = when (dataType) {
            AnalyticsGraphType.SPEED -> runsForSelectedMonth.minOf { it.avgSpeedKmh }
            AnalyticsGraphType.PACE -> runsForSelectedMonth.maxOf { it.pace }
            AnalyticsGraphType.HEART -> runsForSelectedMonth.minOf { it.avgHeartRate!! }
        }

    val valuesRange: Float
        get() = maxValue.toFloat() - minValue.toFloat()

    private val firstDay: Int
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
                AnalyticsGraphType.SPEED -> sortedByDescending { it.avgSpeedKmh }
                AnalyticsGraphType.PACE -> sortedBy { it.pace }
                AnalyticsGraphType.HEART -> sortedByDescending { it.avgHeartRate }
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
                AnalyticsGraphType.SPEED -> run.avgSpeedKmh
                AnalyticsGraphType.PACE -> run.pace
                AnalyticsGraphType.HEART -> run.avgHeartRate!!
            }
        }
}
