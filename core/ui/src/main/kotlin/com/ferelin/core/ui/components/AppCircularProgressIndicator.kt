package com.ferelin.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.theme.AppTheme

@Composable
fun AppCircularProgressIndicator(
    modifier: Modifier = Modifier
) {
    androidx.compose.material.CircularProgressIndicator(
        modifier = modifier,
        color = AppTheme.colors.backgroundSecondary,
        strokeWidth = 2.dp
    )
}