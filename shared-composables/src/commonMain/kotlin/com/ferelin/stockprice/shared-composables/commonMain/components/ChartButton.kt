package com.ferelin.features.about.uiComponents

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
import com.ferelin.stockprice.theme.AppTheme

@Composable
internal fun ChartButton(
  modifier: Modifier = Modifier,
  text: String,
  selected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .size(44.dp)
      .clip(RoundedCornerShape(6.dp))
      .background(
        color = if (selected) {
          com.ferelin.stockprice.theme.AppTheme.colors.buttonSecondary
        } else com.ferelin.stockprice.theme.AppTheme.colors.buttonPrimary
      )
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      style = com.ferelin.stockprice.theme.AppTheme.typography.body2,
      color = if (selected) {
        com.ferelin.stockprice.theme.AppTheme.colors.textPrimary
      } else com.ferelin.stockprice.theme.AppTheme.colors.textSecondary
    )
  }
}