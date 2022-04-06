package com.ferelin.stockprice.sharedComposables.components

import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.sharedComposables.theme.AppTheme

@Composable
fun SettingsDivider(
  modifier: Modifier = Modifier
) {
  Divider(
    modifier = modifier.width(200.dp),
    color = AppTheme.colors.contendSecondary
  )
}