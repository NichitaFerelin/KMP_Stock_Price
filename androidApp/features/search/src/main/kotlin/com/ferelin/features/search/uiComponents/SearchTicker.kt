package com.ferelin.features.search.uiComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.theme.AppTheme

internal val SEARCH_TICKER_HEIGHT = 37.dp

@Composable
internal fun SearchTicker(
  modifier: Modifier = Modifier,
  text: String,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .widthIn(min = 40.dp)
      .height(SEARCH_TICKER_HEIGHT)
      .clip(RoundedCornerShape(12.dp))
      .background(AppTheme.colors.contendSecondary)
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Text(
      modifier = Modifier.padding(horizontal = 6.dp),
      text = text,
      style = AppTheme.typography.body2,
      color = AppTheme.colors.textPrimary
    )
  }
}