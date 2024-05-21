@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.teksiak.analytics.presentation.compare_run

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.teksiak.analytics.domain.DataComparison
import com.teksiak.analytics.presentation.R
import com.teksiak.analytics.presentation.compare_run.components.RunCard
import com.teksiak.analytics.presentation.compare_run.model.CompareDataUi
import com.teksiak.analytics.presentation.compare_run.model.CompareRunsDataUi
import com.teksiak.analytics.presentation.compare_run.model.RunUi
import com.teksiak.core.presentation.designsystem.KeyboardArrowDownIcon
import com.teksiak.core.presentation.designsystem.KeyboardArrowUpIcon
import com.teksiak.core.presentation.designsystem.RuniqueTheme
import com.teksiak.core.presentation.designsystem.components.RuniqueScaffold
import com.teksiak.core.presentation.designsystem.components.RuniqueToolbar
import com.teksiak.core.presentation.designsystem.components.TextDivider
import org.koin.androidx.compose.koinViewModel


@Composable
fun CompareRunScreenRoot(
    onBackClick: () -> Unit,
    viewModel: CompareRunViewModel = koinViewModel(),
) {
    BackHandler {
        if (viewModel.state.compareRunData == null) {
            onBackClick()
        } else {
            viewModel.onAction(CompareRunAction.OnBackClick)
        }
    }

    CompareRunScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                is CompareRunAction.OnBackClick -> {
                    if (viewModel.state.compareRunData == null) {
                        onBackClick()
                    }
                }

                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun CompareRunScreen(
    state: CompareRunState,
    onAction: (CompareRunAction) -> Unit
) {

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState
    )

    RuniqueScaffold(
        topAppBar = {
            RuniqueToolbar(
                title = stringResource(id = R.string.compare_run),
                scrollBehavior = scrollBehavior,
                showBackButton = true,
                onBackClick = { onAction(CompareRunAction.OnBackClick) }
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        withGradient = true
    ) { padding ->
        AnimatedContent(
            targetState = state,
            modifier = Modifier
                .fillMaxSize(),
            label = ""
        ) { state ->
            if (state.compareRunData == null || state.otherRun == null) {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(top = 8.dp)
                        .padding(horizontal = 16.dp),
                ) {
                    state.comparedRun?.let { comparedRun ->
                        RunCard(
                            runUi = comparedRun,
                            modifier = Modifier
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextDivider {
                        Text(
                            text = stringResource(id = R.string.compare_with),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (state.runs.isEmpty()) {
                            item {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = stringResource(id = R.string.no_runs))
                                }
                            }
                        } else {
                            items(state.runs) { run ->
                                RunCard(
                                    runUi = run,
                                    isClickable = true,
                                    onClick = { onAction(CompareRunAction.OnOtherRunChoose(run.id)) }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(padding)
                        .padding(bottom = 16.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    state.comparedRun?.let { comparedRun ->
                        RunCard(
                            runUi = comparedRun
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    CompareDataList(
                        compareRunData = state.compareRunData,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    RunCard(
                        runUi = state.otherRun
                    )
                }
            }
        }
    }
}

@Composable
fun CompareDataList(
    compareRunData: CompareRunsDataUi,
    modifier: Modifier = Modifier
) {
    Column {
        CompareDataRow(
            name = stringResource(id = R.string.duration),
            compareData = compareRunData.duration,
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(8.dp))
        CompareDataRow(
            name = stringResource(id = R.string.distance),
            compareData = compareRunData.distance,
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(8.dp))
        CompareDataRow(
            name = stringResource(id = R.string.pace),
            compareData = compareRunData.pace.copy(
                comparison = when (compareRunData.pace.comparison) {
                    DataComparison.FIRST_BIGGER -> DataComparison.SECOND_BIGGER
                    DataComparison.SECOND_BIGGER -> DataComparison.FIRST_BIGGER
                    DataComparison.EQUALS -> DataComparison.EQUALS
                }
            ),
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(8.dp))
        CompareDataRow(
            name = stringResource(id = R.string.avg_speed),
            compareData = compareRunData.avgSpeed,
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(8.dp))
        CompareDataRow(
            name = stringResource(id = R.string.max_speed),
            compareData = compareRunData.maxSpeed,
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(8.dp))
        CompareDataRow(
            name = stringResource(id = R.string.elevation),
            compareData = compareRunData.elevation,
            modifier = modifier
        )
    }
}

@Composable
fun CompareDataRow(
    name: String,
    compareData: CompareDataUi,
    modifier: Modifier = Modifier
) {
    val (color, icon) = when (compareData.comparison) {
        DataComparison.FIRST_BIGGER -> MaterialTheme.colorScheme.error to KeyboardArrowDownIcon
        DataComparison.SECOND_BIGGER -> MaterialTheme.colorScheme.primary to KeyboardArrowUpIcon
        DataComparison.EQUALS -> MaterialTheme.colorScheme.onSurface to null
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .padding(top = 4.dp, start = 16.dp, end = 8.dp, bottom = 16.dp)
            .height(IntrinsicSize.Min),
    ) {
        Text(
            text = name,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = compareData.data.first,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            VerticalDivider()
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                icon?.let {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = compareData.data.second,
                    color = color,
                    fontSize = 16.sp,
                )
            }
        }
    }
}

@Preview
@Composable
private fun CompareRunScreenPreview() {
    RuniqueTheme {
        CompareRunScreen(
            state = CompareRunState(
                runs = listOf(
                    RunUi(
                        id = "1",
                        dateTime = "2021-10-10",
                        duration = "1h 30m",
                        mapPictureUrl = ""
                    ),
                    RunUi(
                        id = "2",
                        dateTime = "2021-10-11",
                        duration = "1h 20m",
                        mapPictureUrl = ""
                    ),
                    RunUi(
                        id = "3",
                        dateTime = "2021-10-12",
                        duration = "1h 10m",
                        mapPictureUrl = ""
                    ),
                ),
                comparedRun = RunUi(
                    id = "1",
                    dateTime = "2021-10-10",
                    duration = "1h 30m",
                    mapPictureUrl = ""
                )
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun CompareRunScreenPreview2() {
    RuniqueTheme {
        CompareRunScreen(
            state = CompareRunState(
                runs = listOf(
                    RunUi(
                        id = "1",
                        dateTime = "2021-10-10",
                        duration = "1h 30m",
                        mapPictureUrl = ""
                    ),
                    RunUi(
                        id = "2",
                        dateTime = "2021-10-11",
                        duration = "1h 20m",
                        mapPictureUrl = ""
                    ),
                    RunUi(
                        id = "3",
                        dateTime = "2021-10-12",
                        duration = "1h 10m",
                        mapPictureUrl = ""
                    ),
                ),
                comparedRun = RunUi(
                    id = "1",
                    dateTime = "2021-10-10",
                    duration = "1h 30m",
                    mapPictureUrl = ""
                ),
                otherRun = RunUi(
                    id = "2",
                    dateTime = "2021-10-11",
                    duration = "1h 20m",
                    mapPictureUrl = ""
                ),
                compareRunData = CompareRunsDataUi(
                    duration = CompareDataUi(
                        data = "1h 30m" to "1h 20m",
                        comparison = DataComparison.FIRST_BIGGER
                    ),
                    distance = CompareDataUi(
                        data = "10 km" to "12 km",
                        comparison = DataComparison.SECOND_BIGGER
                    ),
                    pace = CompareDataUi(
                        data = "5:00 / km" to "6:00 / km",
                        comparison = DataComparison.SECOND_BIGGER
                    ),
                    avgSpeed = CompareDataUi(
                        data = "10 km/h" to "12 km/h",
                        comparison = DataComparison.SECOND_BIGGER
                    ),
                    maxSpeed = CompareDataUi(
                        data = "15 km/h" to "15 km/h",
                        comparison = DataComparison.EQUALS
                    ),
                    elevation = CompareDataUi(
                        data = "120 m" to "100 m",
                        comparison = DataComparison.FIRST_BIGGER
                    )
                )
            ),
            onAction = {}
        )
    }
}