package com.ferelin.stockprice.desktopApp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ferelin.stockprice.desktopApp.ui.APP_NAV_ITEM_HEIGHT
import com.ferelin.stockprice.desktopApp.ui.APP_NAV_ITEM_WIDTH
import com.ferelin.stockprice.desktopApp.ui.APP_START_PADDING
import com.ferelin.stockprice.sharedComposables.components.ConstrainedText
import com.ferelin.stockprice.sharedComposables.theme.AppTheme

@Composable
fun NavigationBarItem(
  modifier: Modifier = Modifier,
  title: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .height(APP_NAV_ITEM_HEIGHT)
      .width(APP_NAV_ITEM_WIDTH)
      .background(
        color = if (isSelected) {
          AppTheme.colors.backgroundSecondary
        } else AppTheme.colors.backgroundPrimary
      )
      .clickable(onClick = onClick),
    contentAlignment = Alignment.CenterStart
  ) {
    ConstrainedText(
      modifier = Modifier.padding(start = APP_START_PADDING),
      text = title,
      style = AppTheme.typography.title2,
      color = AppTheme.colors.textPrimary
    )
  }
}