package com.ferelin.stockprice.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.components.ConstrainedText
import com.ferelin.stockprice.theme.AppTheme

@Composable
fun SettingsItem(
  modifier: Modifier = Modifier,
  title: String,
  text: String,
  painter: Painter,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp),
    backgroundColor = com.ferelin.stockprice.theme.AppTheme.colors.backgroundPrimary,
    shape = RoundedCornerShape(15.dp),
    elevation = 0.dp
  ) {
    Row(
      modifier = Modifier
        .clickable(onClick = onClick)
        .padding(
          horizontal = 16.dp,
          vertical = 10.dp
        ),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier.weight(0.8f)
      ) {
        ConstrainedText(
          text = title,
          style = AppTheme.typography.body1,
          color = AppTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = text,
          style = com.ferelin.stockprice.theme.AppTheme.typography.body2,
          color = com.ferelin.stockprice.theme.AppTheme.colors.textPrimary
        )
      }
      Icon(
        modifier = Modifier
          .weight(0.2f)
          .padding(end = 16.dp),
        painter = painter,
        contentDescription = null,
        tint = com.ferelin.stockprice.theme.AppTheme.colors.buttonPrimary
      )
    }
  }
}