package com.teksiak.core.presentation.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp

@Composable
fun RuniqueScaffold(
    modifier: Modifier = Modifier,
    topAppBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    withGradient: Boolean = true,
    isBlurred: Boolean = false,
    content: @Composable (PaddingValues) -> Unit
) {
    val blurEffect by animateFloatAsState(
        targetValue = if(isBlurred) 6f else 0f,
        tween(durationMillis = 200),
        label = ""
    )

    Scaffold(
        modifier = modifier
            .blur(blurEffect.dp),
        topBar = topAppBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        if(withGradient) {
            GradientBackground {
                content(padding)
            }
        } else {
            content(padding)
        }

    }
}