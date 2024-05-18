@file:OptIn(ExperimentalMaterial3Api::class)

package com.teksiak.analytics.presentation.compare_run

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.teksiak.analytics.presentation.R
import com.teksiak.analytics.presentation.compare_run.components.RunCard
import com.teksiak.analytics.presentation.compare_run.model.RunUi
import com.teksiak.core.presentation.designsystem.RuniqueTheme
import com.teksiak.core.presentation.designsystem.components.RuniqueScaffold
import com.teksiak.core.presentation.designsystem.components.RuniqueToolbar
import org.koin.androidx.compose.koinViewModel


@Composable
fun CompareRunScreenRoot(
    onBackClick: () -> Unit,
    viewModel: CompareRunViewModel = koinViewModel(),
) {
    CompareRunScreen(
        state = viewModel.state,
        onAction = { action ->
            when(action) {
                is CompareRunAction.OnBackClick -> onBackClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun CompareRunScreen(
    state: CompareRunState,
    onAction: (CompareRunAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    RuniqueScaffold(
        topAppBar = {
            RuniqueToolbar(
                title = stringResource(id = R.string.compare_run),
                showBackButton = true,
                onBackClick = { onAction(CompareRunAction.OnBackClick) }
            )
        }
    ) { padding ->
        if(state.compareRunData == null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                state.comparedRun?.let { comparedRun ->
                    RunCard(
                        runUi = comparedRun,
                        modifier = Modifier
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = stringResource(id = R.string.compare_with),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                RunList(
                    runs = state.runs,
                    onChoose = { runId ->
                        onAction(CompareRunAction.OnOtherRunChoose(runId))
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun RunList(
    runs: List<RunUi>,
    onChoose: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if(runs.isEmpty()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(id = R.string.no_runs))
        }
    }
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(runs) { run ->
            RunCard(
                runUi = run,
                onClick = { onChoose(run.id) }
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
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