package com.ferelin.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.theme.AppTheme

@Composable
fun StockItem(
  modifier: Modifier = Modifier,
  backgroundColor: Color,
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
      .height(75.dp)
      .clickable(onClick = onClick),
    backgroundColor = backgroundColor
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      /*
      Icon
      * */
      Spacer(modifier = Modifier.width(8.dp))
      Column(
        verticalArrangement = Arrangement.Center
      ) {
        Text(text = ticker)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = name)
      }
      Spacer(modifier = Modifier.width(15.dp))
      Icon(
        modifier = Modifier.clickable(onClick = onFavouriteIconClick),
        painter = painterResource(id = R.drawable.ic_favourite_16),
        contentDescription = null,
        tint = if (isFavourite) {
          AppTheme.colors.textPrimary
        } else AppTheme.colors.textSecondary
      )
    }
  }
}