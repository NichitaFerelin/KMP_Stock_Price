package com.ferelin.features.settings.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.theme.AppTheme

@Composable
internal fun SettingsItem(
  modifier: Modifier = Modifier,
  title: String,
  subtitle: String,
  painter: Painter,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .height(100.dp)
      .clickable(onClick = onClick),
    backgroundColor = AppTheme.colors.backgroundPrimary
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(text = title)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = subtitle)
      }
      Spacer(modifier = Modifier.width(4.dp))
      Icon(
        painter = painter,
        contentDescription = "",
        tint = AppTheme.colors.backgroundPrimary
      )
    }
  }
}