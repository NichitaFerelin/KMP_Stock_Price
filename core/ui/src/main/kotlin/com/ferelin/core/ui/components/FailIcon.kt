package com.ferelin.core.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ferelin.core.ui.theme.AppTheme

@Composable
fun BoxScope.FailIcon(
  modifier: Modifier = Modifier
) {
  Icon(
    modifier = Modifier.align(Alignment.Center),
    imageVector = Icons.Default.Close,
    contentDescription = null,
    tint = AppTheme.colors.contendTertiary
  )
}