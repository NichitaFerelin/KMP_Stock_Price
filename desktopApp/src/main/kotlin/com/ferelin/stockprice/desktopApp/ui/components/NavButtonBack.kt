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
import com.ferelin.stockprice.desktopApp.ui.APP_NAV_ITEM_HEIGHT
import com.ferelin.stockprice.desktopApp.ui.APP_NAV_ITEM_WIDTH
import com.ferelin.stockprice.sharedComposables.theme.AppTheme

@Composable
fun NavButtonBack(
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .width(APP_NAV_ITEM_WIDTH)
      .height(APP_NAV_ITEM_HEIGHT)
      .clickable(onClick = onClick),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = Icons.Default.ArrowBack,
      contentDescription = "",
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