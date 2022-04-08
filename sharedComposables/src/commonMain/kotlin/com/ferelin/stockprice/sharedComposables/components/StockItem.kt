package com.ferelin.stockprice.sharedComposables.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.sharedComposables.NativeStockImage
import com.ferelin.stockprice.sharedComposables.theme.AppTheme

@Composable
internal fun StockItem(
  modifier: Modifier = Modifier,
  index: Int,
  iconUrl: String,
  ticker: String,
  name: String,
  isFavourite: Boolean,
  onFavouriteIconClick: () -> Unit,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .height(75.dp),
    backgroundColor = if (index % 2 == 0) {
      AppTheme.colors.contendPrimary
    } else AppTheme.colors.contendSecondary,
    shape = RoundedCornerShape(12.dp),
    elevation = 0.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .clickable(onClick = onClick)
        .padding(horizontal = 14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        modifier = Modifier.fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        NativeStockImage(iconUrl = iconUrl)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          ConstrainedText(
            text = ticker,
            style = AppTheme.typography.body1,
            color = AppTheme.colors.textPrimary
          )
          ConstrainedText(
            text = name,
            style = AppTheme.typography.body2,
            color = AppTheme.colors.textPrimary
          )
        }
      }
      ClickableIcon(
        modifier = Modifier,
        backgroundColor = if (index % 2 == 0) {
          AppTheme.colors.contendPrimary
        } else AppTheme.colors.contendSecondary,
        imageVector = Icons.Default.Star,
        contentDescription = if (isFavourite) {
          "Remove from favourites"
        } else "Add to favourites",
        iconTint = if (isFavourite) {
          AppTheme.colors.iconActive
        } else AppTheme.colors.iconDisabled,
        onClick = onFavouriteIconClick
      )
    }
  }
}