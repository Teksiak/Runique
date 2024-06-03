@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.teksiak.analytics.presentation.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.teksiak.analytics.domain.AnalyticsGraphData
import com.teksiak.analytics.presentation.R
import com.teksiak.analytics.presentation.dashboard.components.AnalyticsCard
import com.teksiak.analytics.presentation.dashboard.components.AnalyticsGraphCard
import com.teksiak.analytics.presentation.dashboard.model.AnalyticsDataUi
import com.teksiak.core.domain.location.Location
import com.teksiak.core.domain.run.Run
import com.teksiak.core.presentation.designsystem.RuniqueTheme
import com.teksiak.core.presentation.designsystem.components.ExpandableRunCard
import com.teksiak.core.presentation.designsystem.components.GradientBackground
import com.teksiak.core.presentation.designsystem.components.RuniqueScaffold
import com.teksiak.core.presentation.designsystem.components.RuniqueToolbar
import org.koin.androidx.compose.koinViewModel
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun AnalyticsDashboardScreenRoot(
    onBackClick: () -> Unit,
    viewModel: AnalyticsDashboardViewModel = koinViewModel()
) {
    AnalyticsDashboardScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                AnalyticsDashboardAction.OnBackClick -> onBackClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun AnalyticsDashboardScreen(
    state: AnalyticsDashboardState?,
    onAction: (AnalyticsDashboardAction) -> Unit,
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState
    )
    
    var selectedRun by remember {
        mutableStateOf<Run?>(null)
    }

    var isRunExpanded by remember {
        mutableStateOf(false)
    }

    var previousSelectedDay by remember {
        mutableStateOf(state?.selectedDay)
    }

    var dayChangeDifference by remember {
        mutableIntStateOf((state?.selectedDay ?: 0) - (previousSelectedDay ?: 0))
    }

    SideEffect {
        if (previousSelectedDay != state?.selectedDay) {
            dayChangeDifference = (state?.selectedDay ?: 0) - (previousSelectedDay ?: 0)
            previousSelectedDay = state?.selectedDay
        }
    }

    LaunchedEffect(key1 = state?.selectedDay) {
        state?.selectedDay?.let {
            selectedRun = state.graphData.runByDay[it]
        }
    }

    GradientBackground {
        RuniqueScaffold(
            topAppBar = {
                RuniqueToolbar(
                    title = stringResource(id = R.string.analytics),
                    showBackButton = true,
                    onBackClick = { onAction(AnalyticsDashboardAction.OnBackClick) }
                )
            },
//            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { padding ->
            if (state == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AnalyticsCard(
                            analyticsData = AnalyticsDataUi(
                                name = stringResource(id = R.string.total_distance),
                                value = state.totalDistance
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        AnalyticsCard(
                            analyticsData = AnalyticsDataUi(
                                name = stringResource(id = R.string.total_duration),
                                value = state.totalDuration
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AnalyticsCard(
                            analyticsData = AnalyticsDataUi(
                                name = stringResource(id = R.string.max_speed),
                                value = state.maxSpeed
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        AnalyticsCard(
                            analyticsData = AnalyticsDataUi(
                                name = stringResource(id = R.string.highest_heart_rate),
                                value = "-"
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    AnalyticsGraphCard(
                        graphData = state.graphData,
                        selectedDay = state.selectedDay,
                        onTypeSelect = { type ->
                            onAction(AnalyticsDashboardAction.OnGraphTypeSelect(type))
                        },
                        onDaySelect = { day ->
                            dayChangeDifference = (state.selectedDay ?: 0) - (day)
                            onAction(AnalyticsDashboardAction.OnDaySelect(day))
                        },
                        onMonthSelect = { month ->
                            onAction(AnalyticsDashboardAction.OnMonthSelect(month))
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AnimatedContent(
                        targetState = selectedRun,
                        transitionSpec = {
                            slideInHorizontally(
                                initialOffsetX = {
                                    if(dayChangeDifference > 0) it else -it
                                }
                            ).togetherWith(
                                slideOutHorizontally(
                                    targetOffsetX = {
                                        if(dayChangeDifference > 0) -it else it
                                    }
                                )
                            )
                        },
                        label = ""
                    ) { selectedRun ->
                        key(selectedRun?.id) {
                            selectedRun?.let {
                                ExpandableRunCard(
                                    run = it,
                                    isExpanded = isRunExpanded,
                                    onClick = {
                                        isRunExpanded = !isRunExpanded
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun AnalyticsDashboardScreenPreview() {
    RuniqueTheme {
        AnalyticsDashboardScreen(
            state = AnalyticsDashboardState(
                totalDistance = "10 km",
                totalDuration = "0d 1h 30m",
                maxSpeed = "15 km/h",
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
                selectedDay = ZonedDateTime.now().dayOfMonth
            ),
            onAction = {}
        )
    }
}