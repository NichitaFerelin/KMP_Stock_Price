package com.ferelin.stockprice.sharedComposables.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.sharedComposables.components.ConstrainedText
import com.ferelin.stockprice.sharedComposables.theme.AppTheme

@Composable
fun NewsItem(
  modifier: Modifier = Modifier,
  source: String,
  url: String,
  date: String,
  title: String,
  content: String,
  onUrlClick: (String) -> Unit
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    elevation = 6.dp,
    backgroundColor = AppTheme.colors.backgroundPrimary
  ) {
    Column(
      modifier = Modifier.padding(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        ConstrainedText(
          text = source,
          style = AppTheme.typography.body2,
          color = AppTheme.colors.textTertiary
        )
        Spacer(modifier = Modifier.width(8.dp))
        ConstrainedText(
          text = date,
          style = AppTheme.typography.body2,
          color = AppTheme.colors.textTertiary
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      ConstrainedText(
        text = title,
        style = AppTheme.typography.title2,
        color = AppTheme.colors.textPrimary
      )
      Spacer(modifier = Modifier.height(8.dp))
      ConstrainedText(
        text = content,
        maxLines = 4,
        style = AppTheme.typography.body1,
        color = AppTheme.colors.textPrimary
      )
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "temp" /*stringResource(R.string.hintOpenInBrowser)*/,
          style = AppTheme.typography.caption1,
          color = AppTheme.colors.textTertiary
        )
        ConstrainedText(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable { onUrlClick(url) }
            .widthIn(max = 150.dp),
          text = url,
          style = AppTheme.typography.caption1,
          color = AppTheme.colors.textClickable
        )
      }
    }
  }
}