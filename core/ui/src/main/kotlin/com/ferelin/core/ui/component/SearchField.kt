package com.ferelin.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.theme.AppTheme

@Composable
fun SearchField(
  modifier: Modifier = Modifier,
  borderWidth: Dp,
  innerContent: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(60.dp)
      .background(AppTheme.colors.backgroundPrimary)
      .clip(RoundedCornerShape(20.dp))
      .border(
        width = borderWidth,
        color = AppTheme.colors.backgroundPrimary
      ),
    contentAlignment = Alignment.CenterStart
  ) {
    innerContent()
  }
}