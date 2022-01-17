package com.ferelin.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.theme.AppTheme
import com.skydoves.landscapist.glide.GlideImage

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
    Box(
      modifier = modifier
        .clickable(onClick = onClick)
        .padding(horizontal = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        GlideImage(
          modifier = Modifier
            .size(50.dp)
            .clip(CircleShape),
          imageModel = iconUrl,
          contentScale = ContentScale.Inside
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
          modifier = Modifier.weight(0.7f),
          verticalArrangement = Arrangement.SpaceAround,
        ) {
          ConstrainedText(
            text = ticker,
            style = AppTheme.typography.title2,
            color = AppTheme.colors.textPrimary
          )
          ConstrainedText(
            text = name,
            style = AppTheme.typography.body2,
            color = AppTheme.colors.textPrimary
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        ClickableIcon(
          modifier = Modifier
            .padding(horizontal = 6.dp)
            .weight(0.3f),
          backgroundColor = if (index % 2 == 0) {
            AppTheme.colors.contendPrimary
          } else AppTheme.colors.contendSecondary,
          painter = painterResource(id = R.drawable.ic_favourite_16),
          contentDescription = "",
          tint = if (isFavourite) {
            AppTheme.colors.iconActive
          } else AppTheme.colors.iconDisabled,
          onClick = onFavouriteIconClick
        )
      }
    }
  }
}

@Preview
@Composable
private fun StockItemLight() {
  AppTheme(useDarkTheme = false) {
    StockItem(
      index = 0,
      iconUrl = "",
      ticker = "AAPL",
      name = "Apple",
      isFavourite = true,
      onFavouriteIconClick = { /**/ },
      onClick = { }
    )
  }
}

@Preview
@Composable
private fun StockItemDark() {
  AppTheme(useDarkTheme = true) {
    StockItem(
      index = 0,
      iconUrl = "",
      ticker = "AAPL",
      name = "Apple",
      isFavourite = true,
      onFavouriteIconClick = { /**/ },
      onClick = { }
    )
  }
}

@Preview
@Composable
private fun StockItemRandom() {
  AppTheme(useDarkTheme = false) {
    StockItem(
      index = 0,
      iconUrl = "",
      ticker = "AAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPL",
      name = "AppleAAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPLAAPL",
      isFavourite = false,
      onFavouriteIconClick = { /**/ },
      onClick = { }
    )
  }
}