package com.ferelin.stockprice.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.theme.AppTheme

@Composable
fun Snackbar(
  modifier: Modifier = Modifier,
  backgroundColor: Color,
  text: String
) {
  Card(
    modifier = modifier
      .height(100.dp)
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 16.dp)
      .clip(RoundedCornerShape(6.dp)),
    backgroundColor = backgroundColor,
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
      contentAlignment = Alignment.CenterStart
    ) {
      Text(
        text = text,
        style = AppTheme.typography.body1,
        color = AppTheme.colors.textPrimary
      )
    }
  }
}