package com.ferelin.stockprice.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.components.ConstrainedText
import com.ferelin.stockprice.theme.AppTheme

@Composable
fun ProfileInfoRow(
  modifier: Modifier = Modifier,
  name: String,
  content: String
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = name,
      style = com.ferelin.stockprice.theme.AppTheme.typography.body1,
      color = com.ferelin.stockprice.theme.AppTheme.colors.textTertiary
    )
    ConstrainedText(
      modifier = Modifier.padding(start = 16.dp),
      text = content,
      style = AppTheme.typography.body1,
      color = AppTheme.colors.textPrimary
    )
  }
}

@Composable
fun ProfileInfoRowClickable(
  modifier: Modifier = Modifier,
  name: String,
  content: String,
  onClick: () -> Unit
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = name,
      style = com.ferelin.stockprice.theme.AppTheme.typography.body1,
      color = com.ferelin.stockprice.theme.AppTheme.colors.textTertiary
    )
    ConstrainedText(
      modifier = Modifier
        .padding(start = 16.dp)
        .clip(RoundedCornerShape(6.dp))
        .clickable(onClick = onClick),
      text = content,
      style = AppTheme.typography.body1,
      color = AppTheme.colors.textClickable
    )
  }
}