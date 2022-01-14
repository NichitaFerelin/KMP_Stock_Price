package com.ferelin.features.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.theme.AppTheme

@Composable
internal fun SearchItem(
  modifier: Modifier = Modifier,
  text: String,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .height(30.dp)
      .clip(RoundedCornerShape(35.dp))
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Text(text = text)
  }
}