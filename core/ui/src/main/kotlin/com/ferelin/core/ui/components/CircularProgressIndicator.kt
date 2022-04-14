package com.ferelin.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.theme.AppTheme

@Composable
fun CircularProgressIndicator() {
    androidx.compose.material.CircularProgressIndicator(
        color = AppTheme.colors.backgroundSecondary,
        strokeWidth = 2.dp
    )
}