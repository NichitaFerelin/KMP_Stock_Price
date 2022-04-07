package com.ferelin.stockprice.desktopApp.ui

import androidx.compose.runtime.*
import com.ferelin.stockprice.desktopApp.ViewModelWrapper
import com.ferelin.stockprice.shared.ui.viewData.StockViewData
import com.ferelin.stockprice.shared.ui.viewModel.StocksStateUi
import com.ferelin.stockprice.shared.ui.viewModel.StocksViewModel
import com.ferelin.stockprice.sharedComposables.components.StocksList

@Composable
internal fun StocksScreenRoute(
  onStockRoute: (StockViewData) -> Unit
) {
  val viewModelScope = rememberCoroutineScope()
  val viewModel: StocksViewModel = remember {
    ViewModelWrapper().viewModel(viewModelScope)
  }
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