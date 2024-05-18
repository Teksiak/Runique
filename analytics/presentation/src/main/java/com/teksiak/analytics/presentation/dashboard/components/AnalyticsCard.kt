package com.teksiak.analytics.presentation.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teksiak.analytics.presentation.dashboard.model.AnalyticsDataUi

@Composable
fun AnalyticsCard(
    analyticsData: AnalyticsDataUi,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = analyticsData.name,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        Text(
            text = analyticsData.value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp
        )


    }
}