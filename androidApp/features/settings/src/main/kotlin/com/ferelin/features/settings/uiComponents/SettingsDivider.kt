package com.ferelin.features.settings.uiComponents

import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.theme.AppTheme

@Composable
internal fun SettingsDivider(
  modifier: Modifier = Modifier
) {
  Divider(
    modifier = modifier.width(200.dp),
    color = AppTheme.colors.contendSecondary
  )
}