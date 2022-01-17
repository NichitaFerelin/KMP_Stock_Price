package com.ferelin.features.about.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.component.ConstrainedText
import com.ferelin.core.ui.theme.AppTheme

@Composable
internal fun ProfileInfoColumn(
  modifier: Modifier = Modifier,
  name: String,
  content: String
) {
  Column(
    modifier = modifier.padding(horizontal = 20.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = name,
      style = AppTheme.typography.body1,
      color = AppTheme.colors.textTertiary
    )
    Spacer(modifier = Modifier.height(12.dp))
    ConstrainedText(
      text = content,
      style = AppTheme.typography.body1,
      color = AppTheme.colors.textPrimary
    )
  }
}