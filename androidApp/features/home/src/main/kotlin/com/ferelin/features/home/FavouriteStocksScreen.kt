package com.ferelin.features.home.favourite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ferelin.stockprice.components.StocksList
import com.ferelin.stockprice.ui.viewData.StockViewData
import com.ferelin.stockprice.ui.viewModel.FavouriteStocksStateUi
import com.ferelin.stockprice.ui.viewModel.FavouriteStocksViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun FavouriteStocksRoute(
  onStockRoute: (StockViewData) -> Unit
) {
  val viewModel = getViewModel<FavouriteStocksViewModel>()
  val uiState by viewModel.uiState.collectAsState()

  FavouriteStocksScreen(
    uiState = uiState,
    onFavouriteIconClick = viewModel::onFavouriteIconClick,
    onStockClick = onStockRoute
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