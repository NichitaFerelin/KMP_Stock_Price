package com.ferelin.features.settings.ui.component

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
    modifier = modifier.width(50.dp),
    color = AppTheme.colors.contendPrimary
  )
}