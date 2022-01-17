package com.ferelin.features.stocks.favourites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.core.ui.component.StocksList

@Composable
fun FavouriteStocksRoute(deps: FavouriteStocksDeps) {
  val component = DaggerFavouriteStocksComponent.builder()
    .dependencies(deps)
    .build()
  val viewModel = viewModel<FavouriteStocksViewModel>(
    factory = component.viewModelFactory()
  )
  val uiState by viewModel.uiState.collectAsState()

  FavouriteStocksScreen(
    uiState = uiState,
    onFavouriteIconClick = viewModel::onFavouriteIconClick,
    onStockClick = { }
  )
}

@Composable
private fun FavouriteStocksScreen(
  uiState: FavouriteStocksStateUi,
  onFavouriteIconClick: (StockViewData) -> Unit,
  onStockClick: (StockViewData) -> Unit
) {
  StocksList(
    stocks = uiState.companies,
    stocksLce = uiState.companiesLce,
    onFavouriteIconClick = onFavouriteIconClick,
    onStockClick = onStockClick
  )
}