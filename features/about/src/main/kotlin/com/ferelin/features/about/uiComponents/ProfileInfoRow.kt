package com.ferelin.features.about.uiComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.components.ConstrainedText
import com.ferelin.core.ui.theme.AppTheme

@Composable
internal fun ProfileInfoRow(
  modifier: Modifier = Modifier,
  name: String,
  content: String
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = name,
      style = AppTheme.typography.body1,
      color = AppTheme.colors.textTertiary
    )
    ConstrainedText(
      modifier = Modifier.padding(start = 16.dp),
      text = content,
      style = AppTheme.typography.body1,
      color = AppTheme.colors.textPrimary
    )
  }
}