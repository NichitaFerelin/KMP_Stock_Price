package com.ferelin.stockprice.sharedComposables.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.sharedComposables.NativeCryptoImage
import com.ferelin.stockprice.sharedComposables.theme.AppTheme

@Composable
fun CryptoItem(
  modifier: Modifier = Modifier,
  name: String,
  iconUrl: String,
  price: String,
  profit: String
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .height(100.dp)
      .padding(horizontal = 12.dp),
    backgroundColor = AppTheme.colors.backgroundPrimary,
    elevation = 2.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      NativeCryptoImage(iconUrl = iconUrl)
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
      ) {
        ConstrainedText(
          text = name,
          style = AppTheme.typography.body2,
          color = AppTheme.colors.textPrimary
        )
        ConstrainedText(
          text = price,
          style = AppTheme.typography.body1,
          color = AppTheme.colors.textPrimary
        )
        ConstrainedText(
          text = profit,
          style = AppTheme.typography.body2,
          color = AppTheme.colors.textPrimary
        )
      }
    }
  }
}