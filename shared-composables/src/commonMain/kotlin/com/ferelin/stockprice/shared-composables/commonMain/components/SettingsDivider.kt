package com.ferelin.stockprice.components

import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.theme.AppTheme

@Composable
fun SettingsDivider(
  modifier: Modifier = Modifier
) {
  Divider(
    modifier = modifier.width(200.dp),
    color = com.ferelin.stockprice.theme.AppTheme.colors.contendSecondary
  )
}