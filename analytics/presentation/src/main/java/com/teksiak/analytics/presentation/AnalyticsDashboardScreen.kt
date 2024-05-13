@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.teksiak.analytics.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.teksiak.analytics.presentation.components.AnalyticsCard
import com.teksiak.analytics.presentation.model.AnalyticsDataUi
import com.teksiak.core.presentation.designsystem.RuniqueTheme
import com.teksiak.core.presentation.designsystem.components.GradientBackground
import com.teksiak.core.presentation.designsystem.components.RuniqueScaffold
import com.teksiak.core.presentation.designsystem.components.RuniqueToolbar
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnalyticsDashboardScreenRoot(
    onBackClick: () -> Unit,
    viewModel: AnalyticsDashboardViewModel = koinViewModel()
) {
    AnalyticsDashboardScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                AnalyticsAction.OnBackClick -> onBackClick()
            }
        }
    )
}

@Composable
fun AnalyticsDashboardScreen(
    state: AnalyticsDashboardState?,
    onAction: (AnalyticsAction) -> Unit,
) {
    GradientBackground {
        RuniqueScaffold(
            topAppBar = {
                RuniqueToolbar(
                    title = stringResource(id = R.string.analytics),
                    showBackButton = true,
                    onBackClick = { onAction(AnalyticsAction.OnBackClick) }
                )
            }
        ) { padding ->
            if(state == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val analyticsDataUiList = listOf(
                    AnalyticsDataUi(
                        name = stringResource(id = R.string.total_distance),
                        value = state.totalDistance
                    ),
                    AnalyticsDataUi(
                        name = stringResource(id = R.string.total_duration),
                        value = state.totalDuration
                    ),
                    AnalyticsDataUi(
                        name = stringResource(id = R.string.max_speed),
                        value = state.maxSpeed
                    ),
                    AnalyticsDataUi(
                        name = stringResource(id = R.string.avg_distance),
                        value = state.avgDistance
                    ),
                    AnalyticsDataUi(
                        name = stringResource(id = R.string.avg_pace),
                        value = state.avgPace
                    ),
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var maxWidth by remember {
                        mutableIntStateOf(0)
                    }
                    val maxWidthDp = with(LocalDensity.current) { maxWidth.toDp() }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        analyticsDataUiList.forEach { analyticsData ->
                            AnalyticsCard(
                                analyticsData = analyticsData,
                                modifier = Modifier
                                    .defaultMinSize(minWidth = maxWidthDp)
                                    .onSizeChanged {
                                        maxWidth = maxOf(maxWidth, it.width)
                                    }
                            )
                        }
                    }
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
                avgDistance = "10 km",
                avgPace = "6 min/km"
            ),
            onAction = {}
        )
    }
}