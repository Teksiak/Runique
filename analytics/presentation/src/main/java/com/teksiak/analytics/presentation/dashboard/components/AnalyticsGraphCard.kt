package com.teksiak.analytics.presentation.dashboard.components

import android.graphics.PointF
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teksiak.analytics.domain.AnalyticsGraphData
import com.teksiak.analytics.domain.AnalyticsGraphType
import com.teksiak.core.domain.location.Location
import com.teksiak.core.domain.run.Run
import com.teksiak.core.presentation.designsystem.KeyboardArrowDownIcon
import com.teksiak.core.presentation.designsystem.RuniqueBlack
import com.teksiak.core.presentation.designsystem.RuniqueTheme
import com.teksiak.core.presentation.ui.toFormattedKmh
import com.teksiak.core.presentation.ui.toFormattedPace
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun AnalyticsGraphCard(
    graphData: AnalyticsGraphData,
    onMonthSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    onTypeSelect: (AnalyticsGraphType) -> Unit,
    selectedDay: Int? = null,
    onDaySelect: (Int) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GraphTypeSelect(
            modifier = Modifier.fillMaxWidth(),
            selectedType = graphData.dataType,
            onTypeSelect = onTypeSelect
        )
        Spacer(modifier = Modifier.height(16.dp))
        AnalyticsGraph(
            graphData = graphData,
            modifier = Modifier.fillMaxWidth(),
            onDaySelect = onDaySelect,
            selectedDay = selectedDay
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (graphData.runs.isNotEmpty()) {
            MonthSelect(
                months = graphData.distinctMonths,
                selectedMonth = graphData.selectedMonth!!,
                onMonthSelect = onMonthSelect
            )
        }
    }
}

@Composable
fun GraphTypeSelect(
    modifier: Modifier = Modifier,
    selectedType: AnalyticsGraphType,
    onTypeSelect: (AnalyticsGraphType) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AnalyticsGraphType.entries.forEach { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = {
                    onTypeSelect(type)
                },
                label = {
                    Text(text = type.title)
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                )
            )
        }
    }

}

@Composable
fun AnalyticsGraph(
    graphData: AnalyticsGraphData,
    modifier: Modifier = Modifier,
    selectedDay: Int? = null,
    onDaySelect: (Int) -> Unit = {},
) {
    val pointByDay: MutableMap<Int, PointF> = mutableMapOf()

    val textMeasurer = rememberTextMeasurer()

    val axisTextStyle = TextStyle(
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 12.sp
    )
    val valueTextStyle = TextStyle(
        color = RuniqueBlack,
        fontSize = 12.sp
    )

    val backgroundLinesColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    val primaryColor = MaterialTheme.colorScheme.primary
    val whiteColor = MaterialTheme.colorScheme.onSurface

    Canvas(
        modifier = modifier
            .height(200.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        val x = offset.x
                        pointByDay.entries
                            .find {
                                it.value.x - 24f <= x && x <= it.value.x + 24f
                            }
                            ?.also {
                                onDaySelect(it.key)
                            }
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        val x = change.position.x
                        pointByDay.entries
                            .find {
                                it.value.x - 24f <= x && x <= it.value.x + 24f
                            }
                            ?.also {
                                onDaySelect(it.key)
                            }
                    }
                )
            }
    ) {

        val graphHeight = size.height - 16.dp.toPx()
        val graphWidth = size.width - 60.dp.toPx()

        graphData.days.forEach { day ->
            val doubleDigitMargin = if (day >= 10) 4.sp.toPx() else 0f
            val x = if (graphData.days.size > 1) {
                graphWidth - ((graphData.lastDay - day) / graphData.daysRange * graphWidth) + 28.dp.toPx() - doubleDigitMargin
            } else {
                size.width / 2 - doubleDigitMargin - 2.sp.toPx()
            }

            drawText(
                textMeasurer = textMeasurer,
                text = day.toString(),
                style = axisTextStyle,
                topLeft = Offset(
                    x = x,
                    y = size.height - 12.sp.toPx()
                ),
            )
        }

        (0..4).forEach {
            drawLine(
                start = Offset(x = size.width / 4 * it, y = 0f),
                end = Offset(x = size.width / 4 * it, y = graphHeight),
                color = backgroundLinesColor,
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(10f, 10f),
                    phase = 0f
                )
            )
        }

        drawLine(
            start = Offset(x = 0f, y = graphHeight),
            end = Offset(x = size.width, y = graphHeight),
            color = backgroundLinesColor,
            strokeWidth = 2f
        )

        graphData.valueByDay.forEach { (day, value) ->
            val x = if (graphData.days.size > 1) {
                graphWidth - ((graphData.lastDay - day) / graphData.daysRange * graphWidth) + 30.dp.toPx()
            } else {
                size.width / 2
            }

            val valueDifference = graphData.maxValue.toFloat() - value.toFloat()
            val y = if (graphData.days.size > 1) {
                valueDifference / graphData.valuesRange * graphHeight * 0.8f + size.height * 0.1f
            } else {
                graphHeight / 2
            }

            pointByDay[day] = PointF(x, y)
        }

        val points = pointByDay.values.toList()
        val controlPoints: MutableList<Pair<PointF, PointF>> = mutableListOf()
        for (i in 1 until pointByDay.size) {
            controlPoints +=
                Pair(
                    PointF(
                        (points[i - 1].x + points[i].x) / 2,
                        points[i - 1].y
                    ),
                    PointF(
                        (points[i - 1].x + points[i].x) / 2,
                        points[i].y
                    ),
                )
        }

        val graphPath = Path().apply {
            reset()
            moveTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
                val (controlPoint1, controlPoint2) = controlPoints[i]
                cubicTo(
                    controlPoint1.x, controlPoint1.y,
                    controlPoint2.x, controlPoint2.y,
                    points[i + 1].x, points[i + 1].y
                )
            }
        }

        drawPath(
            path = graphPath,
            color = primaryColor,
            style = Stroke(
                width = 5f,
                cap = StrokeCap.Round,
            ),
        )

        val fillPath = graphPath.apply {
            lineTo(points.last().x, graphHeight)
            lineTo(points.first().x, graphHeight)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                listOf(
                    primaryColor.copy(alpha = 0.5f),
                    Color.Transparent
                ),
                endY = graphHeight
            )
        )

        selectedDay?.let { day ->
            val point = pointByDay[day]
            point?.let {
                drawLine(
                    start = Offset(x = it.x, y = it.y),
                    end = Offset(x = it.x, y = graphHeight),
                    color = whiteColor,
                    strokeWidth = 4f,
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(15f, 10f),
                        phase = 0f
                    )
                )
            }
        }

        pointByDay.forEach { (day, point) ->
            val (smallRadius, bigRadius) = if (day == selectedDay) {
                10f to 16f
            } else {
                10f to 14f
            }
            drawCircle(
                color = whiteColor,
                radius = bigRadius,
                center = Offset(point.x, point.y)
            )
            drawCircle(
                color = primaryColor,
                radius = smallRadius,
                center = Offset(point.x, point.y)
            )
        }

        selectedDay?.let { day ->
            val formattedValue = when(graphData.dataType) {
                AnalyticsGraphType.SPEED -> (graphData.valueByDay[day] as Double).toFormattedKmh()
                AnalyticsGraphType.PACE -> (graphData.valueByDay[day] as Double).seconds.toFormattedPace()
            }

            val valueSize = textMeasurer.measure(
                formattedValue,
                valueTextStyle
            ).size

            val point = pointByDay[day]!!

            drawRoundRect(
                color = whiteColor,
                topLeft = Offset(
                    x = point.x - valueSize.width / 2 - 16f,
                    y = point.y - 62f - valueSize.height
                ),
                size = Size(
                    width = valueSize.width + 32f,
                    height = valueSize.height + 16f
                ),
                cornerRadius = CornerRadius(
                    16f, 16f
                )
            )

            val path = Path().apply {
                moveTo(point.x + 16f, point.y - 48f)
                lineTo(point.x + 3f, point.y - 34f)
                quadraticBezierTo(
                    point.x, point.y - 30f,
                    point.x - 3f, point.y - 34f
                )
                lineTo(point.x - 16f, point.y - 48f)
                close()
            }
            drawPath(
                path = path,
                color = whiteColor,
            )

            drawText(
                textMeasurer = textMeasurer,
                text = formattedValue,
                style = valueTextStyle,
                topLeft = Offset(
                    x = point.x - valueSize.width / 2,
                    y = point.y - 54f - valueSize.height
                )
            )
        }
    }
}

@Composable
fun MonthSelect(
    months: List<String>,
    selectedMonth: String,
    onMonthSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandMonths by remember {
        mutableStateOf(false)
    }
    val expandIconRotate by animateFloatAsState(
        targetValue = if (expandMonths) 180f else 0f,
        animationSpec = tween(300),
        label = ""
    )

    Box {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    expandMonths = !expandMonths
                }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = selectedMonth,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                modifier = Modifier.rotate(expandIconRotate),
                imageVector = KeyboardArrowDownIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        DropdownMenu(expanded = expandMonths, onDismissRequest = { expandMonths = false }) {
            months.forEach { month ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = month,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    onClick = { onMonthSelect(month) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun AnalyticsGraphCardPreview() {
    RuniqueTheme {
        AnalyticsGraphCard(
            graphData = AnalyticsGraphData(
                runs = listOf(
                    Run(
                        id = "123",
                        duration = 10.minutes + 30.seconds,
                        dateTimeUtc = ZonedDateTime.now(),
                        distanceMeters = 5500,
                        location = Location(0.0, 0.0),
                        maxSpeedKmh = 15.0,
                        totalElevationMeters = 123,
                        mapPictureUrl = null
                    )
                )
            ),
            onDaySelect = {},
            onMonthSelect = {},
            onTypeSelect = {}
        )
    }
}

@Preview
@Composable
private fun AnalyticsGraphCardPreview2() {
    RuniqueTheme {
        AnalyticsGraphCard(
            graphData = AnalyticsGraphData(
                runs = listOf(
                    Run(
                        id = "123",
                        duration = 10.minutes + 30.seconds,
                        dateTimeUtc = ZonedDateTime.of(
                            2021, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")
                        ),
                        distanceMeters = 5500,
                        location = Location(0.0, 0.0),
                        maxSpeedKmh = 15.0,
                        totalElevationMeters = 123,
                        mapPictureUrl = null
                    ),
                    Run(
                        id = "123",
                        duration = 10.minutes + 30.seconds,
                        dateTimeUtc = ZonedDateTime.of(
                            2021, 1, 2, 0, 0, 0, 0, ZoneId.of("UTC")
                        ),
                        distanceMeters = 4500,
                        location = Location(0.0, 0.0),
                        maxSpeedKmh = 15.0,
                        totalElevationMeters = 123,
                        mapPictureUrl = null
                    )
                )
            ),
            onDaySelect = {},
            onMonthSelect = {},
            onTypeSelect = {}
        )
    }
}

@Preview
@Composable
private fun AnalyticsGraphCardPreview3() {
    RuniqueTheme {
        AnalyticsGraphCard(
            graphData = AnalyticsGraphData(
                runs = listOf(
                    Run(
                        id = "123",
                        duration = 10.minutes + 30.seconds,
                        dateTimeUtc = ZonedDateTime.of(
                            2021, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")
                        ),
                        distanceMeters = 5500,
                        location = Location(0.0, 0.0),
                        maxSpeedKmh = 15.0,
                        totalElevationMeters = 123,
                        mapPictureUrl = null
                    ),
                    Run(
                        id = "123",
                        duration = 10.minutes + 30.seconds,
                        dateTimeUtc = ZonedDateTime.of(
                            2021, 1, 2, 0, 0, 0, 0, ZoneId.of("UTC")
                        ),
                        distanceMeters = 4500,
                        location = Location(0.0, 0.0),
                        maxSpeedKmh = 15.0,
                        totalElevationMeters = 123,
                        mapPictureUrl = null
                    ),
                    Run(
                        id = "123",
                        duration = 10.minutes + 30.seconds,
                        dateTimeUtc = ZonedDateTime.of(
                            2021, 1, 3, 0, 0, 0, 0, ZoneId.of("UTC")
                        ),
                        distanceMeters = 6500,
                        location = Location(0.0, 0.0),
                        maxSpeedKmh = 15.0,
                        totalElevationMeters = 123,
                        mapPictureUrl = null
                    )
                )
            ),
            selectedDay = 2,
            onDaySelect = {},
            onMonthSelect = {},
            onTypeSelect = {}
        )
    }
}