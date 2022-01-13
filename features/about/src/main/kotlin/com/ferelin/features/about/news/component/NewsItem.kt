package com.ferelin.features.about.news.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.component.ConstrainedText
import com.ferelin.core.ui.theme.AppTheme

@Composable
internal fun NewsItem(
  modifier: Modifier = Modifier,
  source: String,
  sourceUrl: String,
  date: String,
  title: String,
  content: String
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    elevation = 6.dp,
    backgroundColor = AppTheme.colors.backgroundPrimary
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
      Row {
        Text(text = source)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = date)
      }
      Spacer(modifier = Modifier.height(8.dp))
      ConstrainedText(text = title)
      Spacer(modifier = Modifier.height(4.dp))
      ConstrainedText(
        maxLines = 3,
        text = content
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row {
        Text(text = stringResource(R.string.hintOpenInBrowser))
        Spacer(modifier = Modifier.width(8.dp))
        ConstrainedText(text = sourceUrl)
      }
    }
  }
}