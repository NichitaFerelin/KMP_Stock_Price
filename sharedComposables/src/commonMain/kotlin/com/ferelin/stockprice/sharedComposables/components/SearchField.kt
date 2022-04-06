package com.ferelin.stockprice.sharedComposables.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.sharedComposables.theme.AppTheme

@Composable
fun SearchField(
  modifier: Modifier = Modifier,
  borderWidth: Dp,
  onClick: () -> Unit,
  innerContent: @Composable () -> Unit
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .height(55.dp)
      .padding(horizontal = 20.dp),
    backgroundColor = AppTheme.colors.backgroundPrimary,
    shape = RoundedCornerShape(20.dp),
    border = BorderStroke(
      width = borderWidth,
      color = AppTheme.colors.buttonPrimary
    ),
    elevation = 0.dp
  ) {
    Box(
      modifier = Modifier.clickable(onClick = onClick)
    ) {
      innerContent()
    }
  }
}

/*
@Preview
@Composable
private fun SearchFieldLight() {
  AppTheme(useDarkTheme = false) {
    SearchField(
      borderWidth = 1.dp,
      onClick = { */
/**//*
 }
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Magenta)
      )
    }
  }
}

@Preview
@Composable
private fun SearchFieldDark() {
  AppTheme(useDarkTheme = true) {
    SearchField(
      borderWidth = 1.dp,
      onClick = { */
/**//*
 }
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Magenta)
      )
    }
  }
}*/
