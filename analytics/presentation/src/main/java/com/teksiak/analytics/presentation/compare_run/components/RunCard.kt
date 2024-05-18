package com.teksiak.analytics.presentation.compare_run.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teksiak.analytics.presentation.compare_run.mapper.toRunUi
import com.teksiak.analytics.presentation.compare_run.model.RunUi
import com.teksiak.core.domain.location.Location
import com.teksiak.core.domain.run.Run
import com.teksiak.core.presentation.designsystem.CalendarIcon
import com.teksiak.core.presentation.designsystem.RuniqueTheme
import com.teksiak.core.presentation.designsystem.components.RuniqueMapImage
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun RunCard(
    runUi: RunUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .clickable {
                onClick()
            }
            .padding(16.dp)
    ) {
        RuniqueMapImage(
            imageUrl = runUi.mapPictureUrl
        )
        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            RunningDateSection(
                dateTime = runUi.dateTime
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = runUi.duration,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun RunningDateSection(
    dateTime: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = CalendarIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = dateTime,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
private fun RunCardPreview() {
    RuniqueTheme {
        RunCard(
            runUi = Run(
                id = "123",
                duration = 10.minutes + 30.seconds,
                dateTimeUtc = ZonedDateTime.now(),
                distanceMeters = 5500,
                location = Location(0.0, 0.0),
                maxSpeedKmh = 15.0,
                totalElevationMeters = 123,
                mapPictureUrl = null
            ).toRunUi(),
        )
    }
}