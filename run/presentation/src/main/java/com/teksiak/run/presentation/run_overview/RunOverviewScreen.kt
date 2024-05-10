@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class
)

package com.teksiak.run.presentation.run_overview

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.teksiak.core.domain.location.Location
import com.teksiak.core.domain.run.Run
import com.teksiak.core.presentation.designsystem.AnalyticsIcon
import com.teksiak.core.presentation.designsystem.LogoIcon
import com.teksiak.core.presentation.designsystem.LogoutIcon
import com.teksiak.core.presentation.designsystem.RunIcon
import com.teksiak.core.presentation.designsystem.RuniqueTheme
import com.teksiak.core.presentation.designsystem.components.RuniqueActionButton
import com.teksiak.core.presentation.designsystem.components.RuniqueDialog
import com.teksiak.core.presentation.designsystem.components.RuniqueFloatingActionButton
import com.teksiak.core.presentation.designsystem.components.RuniqueOutlinedActionButton
import com.teksiak.core.presentation.designsystem.components.RuniqueScaffold
import com.teksiak.core.presentation.designsystem.components.RuniqueToolbar
import com.teksiak.core.presentation.designsystem.components.util.ToolbarMenuItem
import com.teksiak.run.presentation.R
import com.teksiak.run.presentation.run_overview.components.ActiveRunBar
import com.teksiak.run.presentation.run_overview.components.RunListItem
import com.teksiak.run.presentation.run_overview.mappers.toRunUi
import org.koin.androidx.compose.koinViewModel
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun RunOverviewScreenRoot(
    onStartRunClick: () -> Unit,
    onStopService: () -> Unit,
    viewModel: RunOverviewViewModel = koinViewModel()
) {
    RunOverviewScreen(
        state = viewModel.state,
        onStopService = onStopService,
        onAction = { action ->
            when (action) {
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
    onStopService: () -> Unit,
    onAction: (RunOverviewAction) -> Unit,
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
            AnimatedContent(
                targetState = state.isRunActive,
                label = ""
            ) { isRunActive ->
                if (isRunActive) {
                    ActiveRunBar(
                        elapsedTime = state.activeRunDuration,
                        onResumeRun = {
                            onAction(RunOverviewAction.OnStartRunClick)
                        },
                        onDiscardRun = {
                            onAction(RunOverviewAction.OnDiscardRunClick)
                        },
                        modifier = Modifier.offset(y = 16.dp)
                    )
                } else {
                    RuniqueFloatingActionButton(
                        icon = RunIcon,
                        onClick = {
                            onAction(RunOverviewAction.OnStartRunClick)
                        }
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(horizontal = 16.dp),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = state.runs,
                key = { it.id }
            ) {
                RunListItem(
                    runUi = it,
                    onDeleteClick = {
                        onAction(RunOverviewAction.OnDeleteRunClick(it))
                    },
                    modifier = Modifier.animateItemPlacement()
                )
            }
            item { 
                Spacer(modifier = Modifier.height(86.dp))
            }
        }

        if (state.isDiscardRunDialogShown) {
            RuniqueDialog(
                title = stringResource(id = R.string.discard_run),
                onDismiss = {
                    onAction(RunOverviewAction.OnDismissDiscardRunDialogClick)
                },
                description = stringResource(id = R.string.discard_run_description),
                primaryAction = {
                    RuniqueOutlinedActionButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.discard),
                        isLoading = false,
                        onClick = {
                            onAction(RunOverviewAction.OnDiscardRunClick)
                            onStopService()
                        }
                    )
                },
                secondaryAction = {
                    RuniqueActionButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.cancel),
                        isLoading = false,
                        onClick = {
                            onAction(RunOverviewAction.OnDismissDiscardRunDialogClick)
                        }
                    )
                }
            )
        }
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
            onStopService = {},
            onAction = {}
        )
    }
}