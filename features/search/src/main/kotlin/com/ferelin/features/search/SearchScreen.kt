package com.ferelin.features.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.component.SearchField
import com.ferelin.core.ui.component.StockItem
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.search.ui.component.SearchItem
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun SearchRoute(searchViewModel: SearchViewModel) {
  val uiState by searchViewModel.uiState.collectAsState()

  SearchScreen(
    searchStateUi = uiState,
    onSearchTextChanged = searchViewModel::onSearchTextChanged,
    onTickerClick = searchViewModel::onTickerClick
  )
}

@Composable
internal fun SearchScreen(
  searchStateUi: SearchStateUi,
  onSearchTextChanged: (String) -> Unit,
  onTickerClick: (SearchViewData) -> Unit
) {
  Column(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary)
  ) {
    SearchField(
      borderWidth = 3.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(all = 8.dp)
      ) {
        Icon(
          painter = painterResource(R.drawable.ic_arrow_back_24),
          contentDescription = stringResource(R.string.descriptionBack)
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextField(
          value = searchStateUi.inputSearchRequest,
          onValueChange = onSearchTextChanged
        )
      }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = stringResource(R.string.titlePopularRequests))
    Spacer(modifier = Modifier.height(8.dp))
    LazyRow {
      items(
        items = searchStateUi.searchRequests
      ) { searchViewData ->
        SearchItem(
          text = searchViewData.text,
          onClick = { onTickerClick.invoke(searchViewData) }
        )
      }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = stringResource(R.string.titlePopularRequests))
    Spacer(modifier = Modifier.height(8.dp))
    LazyRow {
      items(
        items = searchStateUi.popularSearchRequests
      ) { searchViewData ->
        SearchItem(
          text = searchViewData.text,
          onClick = { onTickerClick.invoke(searchViewData) }
        )
      }
    }
    /***************/
    Spacer(modifier = Modifier.height(16.dp))
    LazyRow {
      items(
        items = searchStateUi.searchResults
      ) { stockViewData ->
        StockItem(
          backgroundColor = AppTheme.colors.backgroundPrimary,
          iconUrl = stockViewData.logoUrl,
          ticker = stockViewData.ticker,
          name = stockViewData.name,
          isFavourite = stockViewData.isFavourite,
          onFavouriteIconClick = { },
          onClick = { }
        )
      }
    }
  }
}