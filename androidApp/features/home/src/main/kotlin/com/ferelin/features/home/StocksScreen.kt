package com.ferelin.features.home.stocks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ferelin.stockprice.components.StocksList
import com.ferelin.stockprice.ui.viewData.StockViewData
import com.ferelin.stockprice.ui.viewModel.StocksStateUi
import com.ferelin.stockprice.ui.viewModel.StocksViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun StocksRoute(
  onStockRoute: (StockViewData) -> Unit
) {
  val viewModel = getViewModel<StocksViewModel>()
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