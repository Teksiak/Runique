package com.teksiak.core.presentation.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teksiak.core.presentation.designsystem.RuniqueDarkRed
import com.teksiak.core.presentation.designsystem.RuniqueWhite

@Composable
fun RuniqueMessageSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    isErrorMessage: Boolean = false,
) {
    Snackbar(
        snackbarData = snackbarData,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        containerColor = if(isErrorMessage) RuniqueDarkRed else MaterialTheme.colorScheme.primary,
        contentColor = if(isErrorMessage) RuniqueWhite else MaterialTheme.colorScheme.onPrimary,
        dismissActionContentColor = RuniqueWhite
    )
}