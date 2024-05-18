@file:OptIn(ExperimentalMaterial3Api::class)

package com.teksiak.analytics.presentation.compare_run

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.teksiak.analytics.presentation.R
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
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun CompareRunScreen(
    state: CompareRunState?,
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
    ) {

    }
}

@Preview
@Composable
private fun CompareRunScreenPreview() {
    RuniqueTheme {
        CompareRunScreen(
            state = null,
            onAction = {}
        )
    }
}