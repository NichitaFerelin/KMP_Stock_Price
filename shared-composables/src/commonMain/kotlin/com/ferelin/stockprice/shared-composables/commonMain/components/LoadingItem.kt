package com.ferelin.stockprice.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun LoadingItem(
  modifier: Modifier = Modifier,
  painter: Painter
) {
  Image(
    modifier = modifier.clip(RoundedCornerShape(12.dp)),
    painter = painter,
    contentDescription = null
  )
}