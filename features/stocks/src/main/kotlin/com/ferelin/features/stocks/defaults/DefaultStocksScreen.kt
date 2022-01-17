package com.ferelin.features.stocks.defaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.core.ui.component.StocksList

@Composable
fun DefaultStocksRoute(
  deps: DefaultStocksDeps,
  onStockRoute: (StockViewData) -> Unit
) {
  val component = remember {
    DaggerDefaultStocksComponent.builder()
      .dependencies(deps)
      .build()
  }
  val viewModel = viewModel<DefaultStocksViewModel>(
    factory = component.viewModelFactory()
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