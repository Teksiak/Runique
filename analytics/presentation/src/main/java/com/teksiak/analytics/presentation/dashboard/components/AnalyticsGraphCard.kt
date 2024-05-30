package com.teksiak.analytics.presentation.dashboard.components

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
import androidx.compose.ui.graphics.PathEffect
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

    Canvas(
        modifier = modifier.height(200.dp)
    ) {
        val xAxisSpace = size.width / (graphData.runByDay.size + 1)

        graphData.runByDay.keys.toList().forEachIndexed { index, day ->
            drawText(
                textMeasurer = textMeasurer,
                text = day.toString(),
                style = axisTextStyle,
                topLeft = Offset(
                    x = (index + 1) * xAxisSpace,
                    y = size.height - 12.sp.toPx()
                )
            )
        }

        (0..4).forEach {
            drawLine(
                start = Offset(x = size.width/4 * it, y = 0f),
                end = Offset(x = size.width/4 * it, y = size.height - 16.sp.toPx()),
                color = backgroundLinesColor,
                strokeWidth = 2f,
                pathEffect = dashedPathEffect
            )
        }

        drawLine(
            start = Offset(x = 0f, y = size.height - 16.sp.toPx()),
            end = Offset(x = size.width, y = size.height - 16.sp.toPx()),
            color = backgroundLinesColor,
            strokeWidth = 2f
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