package com.ferelin.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ferelin.core.ui.theme.AppTheme

@Composable
fun ScreenTitle(
    modifier: Modifier = Modifier,
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackField(
            onBackClick = onBackClick
        )
        ConstrainedText(
            text = title,
            style = AppTheme.typography.title2,
            color = AppTheme.colors.textPrimary
        )
    }
}