@file:OptIn(ExperimentalMaterial3Api::class)

package com.teksiak.run.presentation.run_overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teksiak.core.presentation.designsystem.AnalyticsIcon
import com.teksiak.core.presentation.designsystem.CrossIcon
import com.teksiak.core.presentation.designsystem.LogoIcon
import com.teksiak.core.presentation.designsystem.LogoutIcon
import com.teksiak.core.presentation.designsystem.RunIcon
import com.teksiak.core.presentation.designsystem.RuniqueDarkRed
import com.teksiak.core.presentation.designsystem.RuniqueTheme
import com.teksiak.core.presentation.designsystem.StartIcon
import com.teksiak.core.presentation.designsystem.components.RuniqueFloatingActionButton
import com.teksiak.core.presentation.designsystem.components.RuniqueScaffold
import com.teksiak.core.presentation.designsystem.components.RuniqueToolbar
import com.teksiak.core.presentation.designsystem.components.util.ToolbarMenuItem
import com.teksiak.run.presentation.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
    onStartRunClick: () -> Unit,
    viewModel: RunOverviewViewModel = koinViewModel()
) {
    RunOverviewScreen(
        state = viewModel.state,
        onAction = { action ->
            when(action) {
                is RunOverviewAction.OnStartRunClick -> onStartRunClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun RunOverviewScreen(
    state: RunOverviewState,
    onAction: (RunOverviewAction) -> Unit
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState
    )

    RuniqueScaffold(
        topAppBar = {
            RuniqueToolbar(
                title = stringResource(id = R.string.runique),
                scrollBehavior = scrollBehavior,
                startContent = {
                    Icon(
                        imageVector = LogoIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                actions = {
                    IconButton(
                        onClick = {
                            onAction(RunOverviewAction.OnAnalyticsClick)
                        }
                    ) {
                        Icon(
                            imageVector = AnalyticsIcon,
                            contentDescription = stringResource(id = R.string.analytics),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                menuItems = listOf(
                    ToolbarMenuItem(
                        icon = Icons.Outlined.Info,
                        title = stringResource(id = R.string.help),
                        onClick = {}
                    ),
                    ToolbarMenuItem(
                        icon = LogoutIcon,
                        title = stringResource(id = R.string.logout),
                        onClick = {
                            onAction(RunOverviewAction.OnLogoutClick)
                        }
                    ),
                )
            )
        },
        floatingActionButton = {
            if(state.isRunActive) {
                Column(
                    modifier = Modifier
                        .offset(y = (16).dp)
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )
                        )
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp, bottom = 4.dp),
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.run_in_progress),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        TextButton(
                            onClick = {
                                onAction(RunOverviewAction.OnStartRunClick)
                            }
                        ) {
                            Icon(
                                imageVector = StartIcon,
                                contentDescription = stringResource(id = R.string.resume),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.resume),
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        TextButton(
                            onClick = { /* Discard run */ }
                        ) {
                            Icon(
                                imageVector = CrossIcon,
                                contentDescription = stringResource(id = R.string.discard),
                                tint = RuniqueDarkRed,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(14.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.discard),
                                fontSize = 16.sp,
                                color = RuniqueDarkRed
                            )
                        }
                    }
                }
            } else {
                RuniqueFloatingActionButton(
                    icon = RunIcon,
                    onClick = {
                        onAction(RunOverviewAction.OnStartRunClick)
                    }
                )
            }
        }
    ) { padding ->

    }

}

@Preview
@Composable
fun RunOverviewScreenPreview() {
    RuniqueTheme {
        RunOverviewScreen(
            state = RunOverviewState(
                isRunActive = true
            ),
            onAction = {}
        )
    }
}