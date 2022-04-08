package com.ferelin.stockprice.sharedComposables

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.sharedComposables.components.FailIcon
import com.ferelin.stockprice.sharedComposables.theme.AppColors

@Composable
actual fun NativeThemeSettings(
  useDarkTheme: Boolean,
  colors: AppColors
) {
  /*Do nothing*/
}

@Composable
actual fun NativeCryptoImage(
  modifier: Modifier,
  iconUrl: String
) {
  Icon(
    imageVector = Icons.Default.Image,
    contentDescription = "Decorative icon",
    tint = Color.Blue
  )
}

@Composable
actual fun NativeStockImage(
  modifier: Modifier,
  iconUrl: String
) {
  Icon(
    imageVector = Icons.Default.Image,
    contentDescription = "Decorative icon",
    tint = Color.Blue
  )
}