package com.ferelin.stockprice.sharedComposables.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.sharedComposables.theme.AppTheme

@Composable
fun StockItem(
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
        Icon(
          imageVector = Icons.Default.Image,
          contentDescription = "",
          tint = Color.Blue
        )
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
        imageVector = Icons.Default.Star/*painterResource(id = R.drawable.ic_favourite_16)*/,
        contentDescription = "" /*TODO*/,
        iconTint = if (isFavourite) {
          AppTheme.colors.iconActive
        } else AppTheme.colors.iconDisabled,
        onClick = onFavouriteIconClick
      )
    }
  }
}