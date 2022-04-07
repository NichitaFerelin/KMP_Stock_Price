package com.ferelin.stockprice.sharedComposables.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.sharedComposables.theme.AppTheme

@Composable
fun TopSearchField(
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  SearchField(
    modifier = modifier,
    borderWidth = 1.dp,
    onClick = onClick
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Spacer(modifier = Modifier.padding(start = 12.dp))
      Icon(
        imageVector = Icons.Default.Search,
        contentDescription = ""/*stringResource(id = R.string.descriptionImageSearch)*/,
        tint = AppTheme.colors.buttonPrimary
      )
      Spacer(modifier = Modifier.width(12.dp))
      ConstrainedText(
        text = "Find company by name or ticker"/*stringResource(R.string.hintFindCompany)*/,
        style = AppTheme.typography.body1,
        color = AppTheme.colors.textPrimary
      )
    }
  }
}