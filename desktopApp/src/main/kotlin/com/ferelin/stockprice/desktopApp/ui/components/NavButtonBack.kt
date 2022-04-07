package com.ferelin.stockprice.desktopApp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.sharedComposables.theme.AppTheme

@Composable
internal fun NavButtonBack(
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .width(APP_NAV_ITEM_WIDTH)
      .height(APP_NAV_ITEM_HEIGHT)
      .clickable(onClick = onClick)
      .padding(start = APP_START_PADDING),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = Icons.Default.ArrowBack,
      contentDescription = "Navigation back",
      tint = AppTheme.colors.buttonPrimary
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = "Back",
      style = AppTheme.typography.title2,
      color = AppTheme.colors.textPrimary
    )
  }
}