package com.ferelin.features.about.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.theme.AppTheme

@Composable
internal fun ChartButton(
  modifier: Modifier = Modifier,
  name: String,
  selected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .size(44.dp)
      .background(
        color = if (selected) {
          AppTheme.colors.contendPrimary
        } else AppTheme.colors.contendSecondary
      )
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = name,
      style = AppTheme.typography.caption1,
      color = if (selected) {
        AppTheme.colors.textPrimary
      } else AppTheme.colors.textSecondary
    )
  }
}