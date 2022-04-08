package com.ferelin.stockprice.sharedComposables

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.sharedComposables.components.FailIcon
import com.ferelin.stockprice.sharedComposables.theme.AppColors
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.skydoves.landscapist.glide.GlideImage

@Composable
actual fun NativeThemeSettings(
  useDarkTheme: Boolean,
  colors: AppColors
) {
  val sysUiController = rememberSystemUiController()
  SideEffect {
    sysUiController.setSystemBarsColor(
      color = colors.statusBar,
      darkIcons = !useDarkTheme,
    )
  }
}

@Composable
actual fun NativeCryptoImage(
  modifier: Modifier,
  iconUrl: String
) {
  GlideImage(
    modifier = Modifier.size(40.dp),
    imageModel = iconUrl,
    failure = { FailIcon() }
  )
}

@Composable
actual fun NativeStockImage(
  modifier: Modifier,
  iconUrl: String
) {
  GlideImage(
    modifier = Modifier
      .size(50.dp)
      .clip(CircleShape),
    imageModel = iconUrl,
    failure = { FailIcon() }
  )
}