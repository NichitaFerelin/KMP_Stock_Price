package com.ferelin.features.stocks.ui.common.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.theme.AppTheme

@Composable
internal fun CryptoItem(
  modifier: Modifier = Modifier,
  name: String,
  iconUrl: String,
  price: String,
  profit: String
) {
  Card(
    modifier = modifier
      .height(50.dp)
      .width(70.dp),
    backgroundColor = AppTheme.colors.backgroundPrimary,
    elevation = 6.dp
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 2.dp, end = 2.dp, top = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(text = name)
        /*ICON*/
        Spacer(modifier = Modifier.width(2.dp))
        Text(text = name)
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(text = price)
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = profit)
    }
  }
}