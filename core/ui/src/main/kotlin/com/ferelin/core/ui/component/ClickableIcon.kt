package com.ferelin.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.theme.AppTheme

@Composable
fun ClickableIcon(
  modifier: Modifier = Modifier,
  backgroundColor: Color,
  painter: Painter,
  tint: Color,
  contentDescription: String,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier.size(40.dp),
    backgroundColor = backgroundColor,
    shape = CircleShape,
    elevation = 0.dp
  ) {
    Box(
      modifier = Modifier.clickable(onClick = onClick),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        painter = painter,
        contentDescription = contentDescription,
        tint = tint
      )
    }
  }
}

@Preview
@Composable
private fun ClickableIconPreview() {
  AppTheme {
    ClickableIcon(
      backgroundColor = AppTheme.colors.backgroundPrimary,
      painter = painterResource(id = R.drawable.ic_close_24),
      tint = AppTheme.colors.buttonPrimary,
      contentDescription = "",
      onClick = { /**/ }
    )
  }
}