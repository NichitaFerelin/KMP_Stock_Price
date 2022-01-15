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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.theme.AppTheme
import com.skydoves.landscapist.glide.GlideImage

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
      .height(75.dp),
    backgroundColor = backgroundColor,
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
      }
      ClickableIcon(
        modifier = Modifier
          .align(Alignment.CenterEnd)
          .padding(end = 20.dp),
        backgroundColor = backgroundColor,
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