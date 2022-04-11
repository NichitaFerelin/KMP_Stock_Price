package com.ferelin.stockprice.desktopApp.ui

import androidx.compose.runtime.*
import com.ferelin.stockprice.desktopApp.ViewModelWrapper
import com.ferelin.stockprice.shared.ui.viewData.StockViewData
import com.ferelin.stockprice.shared.ui.viewModel.FavouriteStocksStateUi
import com.ferelin.stockprice.shared.ui.viewModel.FavouriteStocksViewModel
import com.ferelin.stockprice.sharedComposables.components.StocksList

@Composable
internal fun FavouriteStocksScreenRoute(
    onStockRoute: (StockViewData) -> Unit
) {
    val viewModelScope = rememberCoroutineScope()
    val viewModel: FavouriteStocksViewModel = remember {
        ViewModelWrapper().viewModel(viewModelScope)
    }
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