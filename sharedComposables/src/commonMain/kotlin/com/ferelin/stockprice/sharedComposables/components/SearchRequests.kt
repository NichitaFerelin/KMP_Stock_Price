package com.ferelin.stockprice.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.domain.entity.LceState
import com.ferelin.stockprice.ui.viewData.SearchViewData

private val START_PADDING = 20.dp

@Composable
fun SearchRequests(
  modifier: Modifier = Modifier,
  title: String,
  searchRequests: List<SearchViewData>,
  searchRequestsLce: LceState,
  onTickerClick: (SearchViewData) -> Unit
) {
  Column(
    modifier = modifier.fillMaxWidth()
  ) {
    Text(
      modifier = Modifier.padding(start = START_PADDING),
      text = title,
      style = com.ferelin.stockprice.theme.AppTheme.typography.title2,
      color = com.ferelin.stockprice.theme.AppTheme.colors.textPrimary
    )
    Spacer(modifier = Modifier.height(10.dp))

    when (searchRequestsLce) {
      is LceState.Content -> {
        if (searchRequests.isNotEmpty()) {
          LazyRow(
            contentPadding = PaddingValues(horizontal = START_PADDING),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(
              items = searchRequests
            ) { searchViewData ->
              SearchTicker(
                text = searchViewData.text,
                onClick = { onTickerClick.invoke(searchViewData) }
              )
            }
          }
        } else {
          Text(
            modifier = Modifier.padding(start = START_PADDING),
            text = "temp"/*stringResource(id = R.string.hintNoSearchResults)*/,
            style = com.ferelin.stockprice.theme.AppTheme.typography.body2,
            color = com.ferelin.stockprice.theme.AppTheme.colors.textPrimary
          )
        }
      }
      else -> {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(SEARCH_TICKER_HEIGHT),
          contentAlignment = Alignment.Center
        ) {
          when (searchRequestsLce) {
            is LceState.Loading -> {
              CircularProgressIndicator(color = com.ferelin.stockprice.theme.AppTheme.colors.contendTertiary)
            }
            is LceState.Error -> {
              Text(
                text = "temp"/*stringResource(id = R.string.errorDownload)*/,
                style = com.ferelin.stockprice.theme.AppTheme.typography.body1,
                color = com.ferelin.stockprice.theme.AppTheme.colors.textPrimary
              )
            }
            else -> Unit
          }
        }
      }
    }
  }
}