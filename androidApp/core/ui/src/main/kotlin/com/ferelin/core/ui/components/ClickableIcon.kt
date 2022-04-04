package com.ferelin.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ClickableIcon(
  modifier: Modifier = Modifier,
  imageVector: ImageVector,
  contentDescription: String,
  backgroundColor: Color,
  iconTint: Color,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .size(DEFAULT_ICON_HOLDER)
      .background(backgroundColor)
      .clip(CircleShape)
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = imageVector,
      contentDescription = contentDescription,
      tint = iconTint
    )
  }
}

@Composable
fun ClickableIcon(
  modifier: Modifier = Modifier,
  painter: Painter,
  contentDescription: String,
  backgroundColor: Color,
  iconTint: Color,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .size(DEFAULT_ICON_HOLDER)
      .background(backgroundColor)
      .clip(CircleShape)
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      painter = painter,
      contentDescription = contentDescription,
      tint = iconTint
    )
  }
}

private val DEFAULT_ICON_HOLDER = 40.dp