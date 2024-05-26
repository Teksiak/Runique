package com.teksiak.run.presentation.run_overview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teksiak.core.presentation.designsystem.CrossIcon
import com.teksiak.core.presentation.designsystem.RuniqueTheme
import com.teksiak.core.presentation.designsystem.StartIcon
import com.teksiak.core.presentation.designsystem.components.RuniqueTextButton
import com.teksiak.run.presentation.R

@Composable
fun ActiveRunBar(
    elapsedTime: String,
    onResumeRun: () -> Unit,
    onDiscardRun: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
            )
            .background(MaterialTheme.colorScheme.background)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f))
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 4.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.active_run) + " - $elapsedTime",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            RuniqueTextButton(
                text = stringResource(id = R.string.resume),
                color = MaterialTheme.colorScheme.primary,
                leadingIcon = StartIcon,
                onClick = onResumeRun
            )
            RuniqueTextButton(
                text = stringResource(id = R.string.discard),
                color = MaterialTheme.colorScheme.error,
                leadingIcon = CrossIcon,
                onClick = onDiscardRun
            )
        }
    }
}

@Preview
@Composable
private fun ActiveRunBarPreview() {
    RuniqueTheme {
        ActiveRunBar(
            elapsedTime = "00:03:50",
            onResumeRun = {},
            onDiscardRun = {}
        )
    }
}