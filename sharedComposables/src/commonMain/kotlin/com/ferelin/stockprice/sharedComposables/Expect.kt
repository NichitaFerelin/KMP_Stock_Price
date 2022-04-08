package com.ferelin.stockprice.sharedComposables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ferelin.stockprice.sharedComposables.theme.AppColors

// https://youtrack.jetbrains.com/issue/KTIJ-18408

@Composable
expect fun NativeThemeSettings(
  useDarkTheme: Boolean,
  colors: AppColors
)

@Composable
expect fun NativeCryptoImage(
  modifier: Modifier = Modifier,
  iconUrl: String
)

@Composable
expect fun NativeStockImage(
  modifier: Modifier = Modifier,
  iconUrl: String
)