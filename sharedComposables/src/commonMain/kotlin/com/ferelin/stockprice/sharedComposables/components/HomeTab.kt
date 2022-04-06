package com.ferelin.stockprice.sharedComposables.components

import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ferelin.stockprice.sharedComposables.theme.AppTheme

@Composable
fun HomeTab(
  modifier: Modifier = Modifier,
  title: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Tab(
    modifier = modifier,
    text = {
      Text(
        text = title,
        style = AppTheme.typography.title2,
        color = AppTheme.colors.textTertiary
      )
    },
    selected = isSelected,
    onClick = onClick,
  )
}