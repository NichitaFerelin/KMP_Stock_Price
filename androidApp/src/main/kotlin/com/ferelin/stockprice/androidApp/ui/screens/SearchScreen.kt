@file:OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)

package com.ferelin.stockprice.androidApp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.androidApp.ui.ViewModelWrapper
import com.ferelin.stockprice.shared.domain.entity.LceState
import com.ferelin.stockprice.shared.ui.viewData.SearchViewData
import com.ferelin.stockprice.shared.ui.viewData.StockViewData
import com.ferelin.stockprice.shared.ui.viewModel.SearchStateUi
import com.ferelin.stockprice.shared.ui.viewModel.SearchViewModel
import com.ferelin.stockprice.sharedComposables.components.*
import com.ferelin.stockprice.sharedComposables.theme.AppTheme
import com.google.accompanist.insets.statusBarsPadding
import org.koin.androidx.compose.getViewModel

@Composable
fun SearchRoute(
  onBackRoute: () -> Unit,
  onStockRoute: (StockViewData) -> Unit
) {
  val viewModelWrapper = getViewModel<ViewModelWrapper>()
  val viewModel: SearchViewModel = remember { viewModelWrapper.viewModel() }
  val uiState by viewModel.uiState.collectAsState()

  SearchScreen(
    uiState = uiState,
    onSearchTextChanged = viewModel::onSearchTextChanged,
    onTickerClick = viewModel::onTickerClick,
    onStockClick = onStockRoute,
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
  BackHandler(enabled = uiState.inputSearchRequest.isNotEmpty()) {
    onSearchTextChanged("")
  }

  Column(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary)
  ) {
    Spacer(modifier = Modifier.height(8.dp))
    TopSearchFieldEditable(
      inputText = uiState.inputSearchRequest,
      showCloseIcon = uiState.showCloseIcon,
      onTextChanged = onSearchTextChanged,
      onBackClick = onBackClick
    )

    when {
      uiState.searchResultsLce is LceState.Loading -> {
        Box(
          modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator(color = AppTheme.colors.contendTertiary)
        }
      }
      uiState.inputSearchRequest.isNotEmpty() && uiState.searchResults.isEmpty() -> {
        Spacer(modifier = Modifier.height(30.dp))
        NoSearchResults()
      }
      uiState.inputSearchRequest.isEmpty() -> {
        SearchRequestsSection(
          searchRequests = uiState.searchRequests,
          searchRequestsLce = uiState.searchRequestsLce,
          popularSearchRequests = uiState.popularSearchRequests,
          popularSearchRequestsLce = uiState.popularSearchRequestsLce,
          onTickerClick = onTickerClick
        )
      }
      uiState.searchResults.isNotEmpty() -> {
        SearchResultsSection(
          searchResults = uiState.searchResults,
          onFavouriteIconClick = onFavouriteIconClick,
          onStockClick = onStockClick
        )
      }
    }
  }
}

@Composable
private fun SearchResultsSection(
  searchResults: List<StockViewData>,
  onFavouriteIconClick: (StockViewData) -> Unit,
  onStockClick: (StockViewData) -> Unit
) {
  Spacer(modifier = Modifier.height(8.dp))
  StocksList(
    stocks = searchResults,
    stocksLce = LceState.Content,
    onFavouriteIconClick = onFavouriteIconClick,
    onStockClick = onStockClick
  )
}