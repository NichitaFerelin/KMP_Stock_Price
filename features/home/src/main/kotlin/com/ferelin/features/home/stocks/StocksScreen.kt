package com.ferelin.features.home.stocks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferelin.core.ui.components.StocksList
import com.ferelin.core.ui.viewData.StockViewData

@Composable
fun StocksRoute(
  deps: StocksDeps,
  onStockRoute: (StockViewData) -> Unit
) {
  val componentViewModel = viewModel<StocksComponentViewModel>(
    factory = StocksComponentViewModelFactory(deps)
  )
  val viewModel = viewModel<StocksViewModel>(
    factory = componentViewModel.component.viewModelFactory()
  )
  val uiState by viewModel.uiState.collectAsState()

  StocksScreen(
    uiState = uiState,
    onFavouriteIconClick = viewModel::onFavouriteIconClick,
    onStockClick = onStockRoute
  )
}

@Composable
private fun StocksScreen(
  uiState: StocksStateUi,
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