package com.teksiak.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.teksiak.core.presentation.designsystem.RuniqueTheme

@Composable
fun RuniqueDialog(
    title: String,
    onDismiss: () -> Unit,
    description: String,
    modifier: Modifier = Modifier,
    primaryAction: @Composable RowScope.() -> Unit = {},
    secondaryAction: @Composable RowScope.() -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                primaryAction()
                secondaryAction()
            }
        }
    }
}

@Preview
@Composable
private fun RuniqueDialogPreview() {
    RuniqueTheme {
        RuniqueDialog(
            title = "Dialog title",
            onDismiss = {},
            description = "Short description of the dialog",
            primaryAction = {
                RuniqueOutlinedActionButton(
                    modifier = Modifier.weight(1f),
                    text = "Primo",
                    isLoading = false,
                    onClick = { }
                )
            },
            secondaryAction = {
                RuniqueActionButton(
                    modifier = Modifier.weight(1f),
                    text = "Secondo",
                    isLoading = false,
                    onClick = { }
                )
            }
        )
    }
}