package com.mateusavila.gymtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Thin bar filled with the accent color up to [percentage] (0 to 100). */
@Composable
fun ProgressBar(
    percentage: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(percentage.coerceIn(0, 100) / 100f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
    }
}
