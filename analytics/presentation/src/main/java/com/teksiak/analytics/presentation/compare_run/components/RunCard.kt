package com.teksiak.analytics.presentation.compare_run.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teksiak.analytics.presentation.R
import com.teksiak.analytics.presentation.compare_run.mapper.toRunUi
import com.teksiak.analytics.presentation.compare_run.model.RunUi
import com.teksiak.core.domain.location.Location
import com.teksiak.core.domain.run.Run
import com.teksiak.core.presentation.designsystem.CalendarIcon
import com.teksiak.core.presentation.designsystem.LocationIcon
import com.teksiak.core.presentation.designsystem.RunOutlinedIcon
import com.teksiak.core.presentation.designsystem.RuniqueTheme
import com.teksiak.core.presentation.designsystem.components.RunMapImage
import com.teksiak.core.presentation.ui.getLocationName
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun RunCard(
    run: Run,
    modifier: Modifier = Modifier,
    showRunningTime: Boolean = true,
    isClickable: Boolean = false,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current

    val runUi = remember {
        run.toRunUi()
    }

    var locationName by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(key1 = true) {
        run.getLocationName(
            context = context,
        ) { name ->
            locationName = name
        }
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .then(
                if (isClickable) Modifier.clickable { onClick() }
                else Modifier
            )
            .padding(16.dp)
    ) {
        RunMapImage(
            imageUrl = runUi.mapPictureUrl
        )
        Spacer(modifier = Modifier.height(16.dp))


        if(showRunningTime) {
            RunningTimeSection(
                duration = runUi.duration,
            )

            Spacer(modifier = Modifier.height(16.dp))

        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        RunningDateSection(
            dateTime = runUi.dateTime
        )


        locationName?.let { location ->
            Spacer(modifier = Modifier.height(16.dp))

            LocationNameSection(
                locationName = location,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RunningTimeSection(
    duration: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = RunOutlinedIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.running_time),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = duration,
                color = MaterialTheme.colorScheme.onSurface,
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

@Composable
fun LocationNameSection(
    locationName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = LocationIcon,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = locationName,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
        )
    }
}

@Preview
@Composable
private fun RunCardPreview() {
    RuniqueTheme {
        RunCard(
            run = Run(
                id = "123",
                duration = 10.minutes + 30.seconds,
                dateTimeUtc = ZonedDateTime.now(),
                distanceMeters = 5500,
                location = Location(0.0, 0.0),
                maxSpeedKmh = 15.0,
                totalElevationMeters = 123,
                mapPictureUrl = null
            ),
        )
    }
}