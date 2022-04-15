package com.ferelin.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.theme.AppTheme

@Composable
fun BoxScope.AppFab(
    modifier: Modifier = Modifier,
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = AppTheme.colors.backgroundSecondary,
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painter,
                contentDescription = contentDescription,
                tint = AppTheme.colors.buttonPrimary
            )
        }
    }
}