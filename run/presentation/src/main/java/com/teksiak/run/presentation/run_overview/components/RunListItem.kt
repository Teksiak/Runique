@file:OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)

package com.teksiak.run.presentation.run_overview.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import coil.compose.SubcomposeAsyncImage
import com.teksiak.core.domain.location.Location
import com.teksiak.core.domain.run.Run
import com.teksiak.core.presentation.designsystem.CalendarIcon
import com.teksiak.core.presentation.designsystem.CompareIcon
import com.teksiak.core.presentation.designsystem.RunOutlinedIcon
import com.teksiak.core.presentation.designsystem.RuniqueTheme
import com.teksiak.run.presentation.R
import com.teksiak.run.presentation.run_overview.mappers.toRunUi
import com.teksiak.run.presentation.run_overview.model.RunDataUi
import com.teksiak.run.presentation.run_overview.model.RunUi
import timber.log.Timber
import java.time.ZonedDateTime
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun RunListItem(
    runUi: RunUi,
    modifier: Modifier = Modifier,
    isFocused: Boolean,
    onFocusChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    onCompareClick: () -> Unit,
) {
    var showDeletePopup by remember {
        mutableStateOf(false)
    }

    var expandInfo by remember {
        mutableStateOf(false)
    }
    val expandIconRotate by animateFloatAsState(
        targetValue = if (expandInfo) 180f else 0f,
        animationSpec = tween(300),
        label = ""
    )


    LaunchedEffect(key1 = isFocused) {
        showDeletePopup = isFocused
    }

    Box {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                .pointerInput(isFocused) {
                    detectTapGestures(
                        onTap = {
                            expandInfo = !expandInfo
                        },
                        onPress = { _ ->
                            if (!isFocused) {
                                onFocusChange(false)
                            }
                        },
                        onLongPress = {
                            onFocusChange(true)
                        }
                    )
                }
                .padding(16.dp)
        ) {
            MapImage(
                imageUrl = runUi.mapPictureUrl,
            )
            Spacer(modifier = Modifier.height(16.dp))

            RunningTimeSection(
                duration = runUi.duration,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RunningDateSection(
                    dateTime = runUi.dateTime
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { expandInfo = !expandInfo }) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.rotate(expandIconRotate)
                    )
                }
            }

            AnimatedVisibility(
                visible = expandInfo,
                enter = expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = tween(300)
                ),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Top,
                    animationSpec = tween(300)
                )
            ) {
                DataGrid(
                    run = runUi,
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }
        RunActionPopup(
            isVisible = showDeletePopup,
            onDismissRequest = { onFocusChange(false) },
            onDeleteClick = onDeleteClick,
            onCompareClick = onCompareClick
        )
    }
}

@Composable
private fun MapImage(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = stringResource(id = R.string.run_map),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f)
            .clip(RoundedCornerShape(16.dp)),
        loading = {
            Box(
                modifier = modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        error = {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.error_loading_image),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
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
private fun DataGrid(
    run: RunUi,
    modifier: Modifier = Modifier
) {
    val runDataUiList = listOf(
        RunDataUi(
            name = stringResource(id = R.string.distance),
            value = run.distance
        ),
        RunDataUi(
            name = stringResource(id = R.string.pace),
            value = run.pace
        ),
        RunDataUi(
            name = stringResource(id = R.string.avg_speed),
            value = run.avgSpeed
        ),
        RunDataUi(
            name = stringResource(id = R.string.max_speed),
            value = run.maxSpeed
        ),
        RunDataUi(
            name = stringResource(id = R.string.total_elevation),
            value = run.totalElevation
        ),
    )

    var maxWidth by remember {
        mutableIntStateOf(0)
    }
    val maxWidthDp = with(LocalDensity.current) { maxWidth.toDp() }

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        runDataUiList.forEach { runData ->
            DataGridCell(
                runData = runData,
                modifier = Modifier
                    .defaultMinSize(minWidth = maxWidthDp)
                    .onSizeChanged {
                        maxWidth = maxOf(maxWidth, it.width)
                    }
            )
        }

    }
}

@Composable
private fun DataGridCell(
    runData: RunDataUi,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = runData.name,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = runData.value,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun RunActionPopup(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
    onCompareClick: () -> Unit
) {
    val topOffset = with(LocalDensity.current) { 92.dp.toPx() }
    Popup(
        alignment = Alignment.BottomCenter,
        offset = IntOffset(0, topOffset.roundToInt()),
        properties = PopupProperties(
            clippingEnabled = false,
        ),
        onDismissRequest = onDismissRequest
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PopupAction(
                    text = {
                        Text(
                            text = stringResource(id = R.string.compare),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = CompareIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    },
                    onClick = {
                        onCompareClick()
                        onDismissRequest()
                    },
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
                )
                PopupAction(
                    text = {
                        Text(
                            text = stringResource(id = R.string.delete),
                            color = MaterialTheme.colorScheme.error,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                        )
                    },
                    onClick = {
                        onDeleteClick()
                        onDismissRequest()
                    },
                    color = MaterialTheme.colorScheme.errorContainer
                )
            }
        }
    }
}

@Composable
fun PopupAction(
    modifier: Modifier = Modifier,
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.background,
    leadingIcon: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.background)
            .background(color)
            .clickable {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        leadingIcon()
        Spacer(modifier = Modifier.width(8.dp))
        text()
    }
}

@Preview
@Composable
private fun RunListItemPreview() {
    RuniqueTheme {
        RunListItem(
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
            isFocused = false,
            onCompareClick = {},
            onDeleteClick = {},
            onFocusChange = {}
        )
    }
}