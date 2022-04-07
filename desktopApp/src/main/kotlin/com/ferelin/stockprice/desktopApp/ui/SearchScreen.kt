package com.ferelin.stockprice.desktopApp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.desktopApp.ViewModelWrapper
import com.ferelin.stockprice.desktopApp.ui.components.APP_START_PADDING
import com.ferelin.stockprice.desktopApp.ui.components.APP_TOP_PADDING
import com.ferelin.stockprice.desktopApp.ui.components.NavButtonBack
import com.ferelin.stockprice.shared.domain.entity.LceState
import com.ferelin.stockprice.shared.ui.viewData.SearchViewData
import com.ferelin.stockprice.shared.ui.viewData.StockViewData
import com.ferelin.stockprice.shared.ui.viewModel.SearchStateUi
import com.ferelin.stockprice.shared.ui.viewModel.SearchViewModel
import com.ferelin.stockprice.sharedComposables.components.NoSearchResults
import com.ferelin.stockprice.sharedComposables.components.SearchRequestsSection
import com.ferelin.stockprice.sharedComposables.components.StocksList
import com.ferelin.stockprice.sharedComposables.components.TopSearchFieldEditable
import com.ferelin.stockprice.sharedComposables.theme.AppTheme

@Composable
internal fun SearchScreenRoute(
  onBackRoute: () -> Unit,
) {
  val viewModelScope = rememberCoroutineScope()
  val viewModel: SearchViewModel = remember { ViewModelWrapper().viewModel(viewModelScope) }
  val uiState by viewModel.uiState.collectAsState()

  SearchScreen(
    uiState = uiState,
    onSearchTextChanged = viewModel::onSearchTextChanged,
    onTickerClick = viewModel::onTickerClick,
    onStockClick = { /**/ },
    onFavouriteIconClick = viewModel::onFavouriteIconClick,
    onBackClick = onBackRoute
  )
}

@Composable
private fun SearchScreen(
  uiState: SearchStateUi,
  onSearchTextChanged: (String) -> Unit,
  onTickerClick: (SearchViewData) -> Unit,
  onStockClick: (StockViewData) -> Unit,
  onFavouriteIconClick: (StockViewData) -> Unit,
  onBackClick: () -> Unit,
) {
  Row {
    NavigationBar(
      modifier = Modifier.padding(top = APP_TOP_PADDING),
      onBackClick = onBackClick
    )
    Column {
      TopSearchFieldEditable(
        modifier = Modifier.padding(top = APP_TOP_PADDING),
        inputText = uiState.inputSearchRequest,
        showCloseIcon = uiState.showCloseIcon,
        onTextChanged = onSearchTextChanged,
        onBackClick = onBackClick
      )
      Spacer(modifier = Modifier.height(6.dp))
      SearchRequestsSection(
        searchRequests = uiState.searchRequests,
        searchRequestsLce = uiState.searchRequestsLce,
        popularSearchRequests = uiState.popularSearchRequests,
        popularSearchRequestsLce = uiState.popularSearchRequestsLce,
        onTickerClick = onTickerClick
      )
      Spacer(modifier = Modifier.height(12.dp))

      when {
        uiState.searchResultsLce is LceState.Loading -> {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            CircularProgressIndicator(color = AppTheme.colors.contendTertiary)
          }
        }
        uiState.inputSearchRequest.isNotEmpty() && uiState.searchResults.isEmpty() -> {
          Spacer(modifier = Modifier.height(30.dp))
          NoSearchResults()
        }
        else -> {
          Spacer(modifier = Modifier.height(8.dp))
          StocksList(
            stocks = uiState.searchResults,
            stocksLce = LceState.Content,
            onFavouriteIconClick = onFavouriteIconClick,
            onStockClick = onStockClick
          )
        }
      }
    }
  }
}

@Composable
private fun NavigationBar(
  modifier: Modifier = Modifier,
  onBackClick: () -> Unit
) {
  Column(
    modifier = modifier
  ) {
    Text(
      modifier = Modifier.padding(start = APP_START_PADDING),
      text = "Search",
      style = AppTheme.typography.title1,
      color = AppTheme.colors.textPrimary
    )
    Spacer(modifier = Modifier.height(24.dp))
    NavButtonBack(onClick = onBackClick)
  }
}