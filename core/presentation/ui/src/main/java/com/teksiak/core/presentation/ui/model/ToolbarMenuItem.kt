package com.teksiak.core.presentation.ui.model

import androidx.compose.ui.graphics.vector.ImageVector

data class ToolbarMenuItem(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit
)
