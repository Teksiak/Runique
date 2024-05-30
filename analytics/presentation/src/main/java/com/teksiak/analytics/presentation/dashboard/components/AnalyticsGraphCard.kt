package com.teksiak.analytics.presentation.dashboard.components

import android.graphics.PointF
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teksiak.analytics.domain.AnalyticsGraphData
import com.teksiak.core.domain.location.Location
import com.teksiak.core.domain.run.Run
import com.teksiak.core.presentation.designsystem.KeyboardArrowDownIcon
import com.teksiak.core.presentation.designsystem.RuniqueTheme
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun AnalyticsGraphCard(
    graphData: AnalyticsGraphData,
    onMonthChoose: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnalyticsGraph(
            graphData = graphData,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (graphData.runs.isNotEmpty()) {
            MonthChooser(
                months = graphData.distinctMonths,
                selectedMonth = graphData.selectedMonth!!,
                onMonthChoose = onMonthChoose
            )
        }
    }
}

@Composable
fun AnalyticsGraph(
    graphData: AnalyticsGraphData,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    val axisTextStyle = TextStyle(
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 12.sp
    )

    val backgroundLinesColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    val dashedPathEffect = PathEffect.dashPathEffect(
        intervals = floatArrayOf(10f, 10f),
        phase = 0f
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val whiteColor = MaterialTheme.colorScheme.onSurface

    Canvas(
        modifier = modifier.height(200.dp)
    ) {

        val graphHeight = size.height - 16.dp.toPx()
        val graphWidth = size.width - 48.dp.toPx()

        graphData.days.forEachIndexed { index, day ->
            val doubleDigitMargin = if (day >= 10) 6.sp.toPx() else 0f
            val x = if (graphData.days.size > 1) {
                graphWidth - ((graphData.lastDay - day) / graphData.daysRange * graphWidth) + 22.dp.toPx() - doubleDigitMargin
            } else {
                size.width / 2 - doubleDigitMargin
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
                pathEffect = dashedPathEffect
            )
        }

        drawLine(
            start = Offset(x = 0f, y = graphHeight),
            end = Offset(x = size.width, y = graphHeight),
            color = backgroundLinesColor,
            strokeWidth = 2f
        )

        Log.d("AnalyticsGraph", "values: ${graphData.valueByDay}")

        val points: MutableList<PointF> = mutableListOf()

        graphData.valueByDay.forEach { (day, value) ->
            val x = if (graphData.days.size > 1) {
                graphWidth - ((graphData.lastDay - day) / graphData.daysRange * graphWidth) + 24.dp.toPx()
            } else {
                size.width / 2
            }

            val valueDifference = graphData.maxValue.toFloat() - value.toFloat()
            val y = if (graphData.days.size > 1) {
                valueDifference / graphData.valuesRange * graphHeight * 0.8f + size.height * 0.1f
            } else {
                graphHeight / 2
            }

            points += PointF(x, y)
        }

        val controlPoints: MutableList<Pair<PointF, PointF>> = mutableListOf()
        for (i in 1 until points.size) {
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
                    points[i+1].x, points[i+1].y
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

        points.forEach { point ->
            drawCircle(
                color = whiteColor,
                radius = 14f,
                center = Offset(point.x, point.y)
            )

            drawCircle(
                color = primaryColor,
                radius = 10f,
                center = Offset(point.x, point.y)
            )
        }

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

    }
}

@Composable
fun MonthChooser(
    months: List<String>,
    selectedMonth: String,
    onMonthChoose: (String) -> Unit,
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
                    onClick = { onMonthChoose(month) }
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
            onMonthChoose = {},
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
            onMonthChoose = {},
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
            onMonthChoose = {},
        )
    }
}