package com.ferelin.features.stocks.defaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferelin.core.ui.components.StocksList
import com.ferelin.core.ui.viewData.StockViewData

@Composable
fun DefaultStocksRoute(
  deps: DefaultStocksDeps,
  onStockRoute: (StockViewData) -> Unit
) {
  val componentViewModel = viewModel<DefaultStocksComponentViewModel>(
    factory = DefaultStocksComponentViewModelFactory(deps)
  )
  val viewModel = viewModel<DefaultStocksViewModel>(
    factory = componentViewModel.component.viewModelFactory()
  )
  val uiState by viewModel.uiState.collectAsState()

  DefaultStocksScreen(
    uiState = uiState,
    onFavouriteIconClick = viewModel::onFavouriteIconClick,
    onStockClick = onStockRoute
  )
}

@Composable
private fun DefaultStocksScreen(
  uiState: DefaultStocksStateUi,
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