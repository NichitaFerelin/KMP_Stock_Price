package com.ferelin.stockprice.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun ProfileInfoColumn(
  modifier: Modifier = Modifier,
  name: String,
  content: String
) {
  Column(
    modifier = modifier.padding(horizontal = 20.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = name,
      style = com.ferelin.stockprice.theme.AppTheme.typography.body1,
      color = com.ferelin.stockprice.theme.AppTheme.colors.textTertiary
    )
    Spacer(modifier = Modifier.height(12.dp))
    ConstrainedText(
      text = content,
      style = com.ferelin.stockprice.theme.AppTheme.typography.body1,
      color = com.ferelin.stockprice.theme.AppTheme.colors.textPrimary
    )
  }
}

@Composable
fun ProfileInfoColumnClickable(
  modifier: Modifier = Modifier,
  name: String,
  content: String,
  onClick: () -> Unit
) {
  Column(
    modifier = modifier.padding(horizontal = 20.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = name,
      style = com.ferelin.stockprice.theme.AppTheme.typography.body1,
      color = com.ferelin.stockprice.theme.AppTheme.colors.textTertiary
    )
    Spacer(modifier = Modifier.height(12.dp))
    ConstrainedText(
      modifier = Modifier
        .clip(RoundedCornerShape(6.dp))
        .clickable(onClick = onClick),
      text = content,
      style = com.ferelin.stockprice.theme.AppTheme.typography.body1,
      color = com.ferelin.stockprice.theme.AppTheme.colors.textClickable
    )
  }
}