package com.ferelin.stockprice.androidApp.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.ferelin.stockprice.androidApp.ui.ViewModelWrapper
import com.ferelin.stockprice.shared.ui.viewData.StockViewData
import com.ferelin.stockprice.shared.ui.viewModel.FavouriteStocksStateUi
import com.ferelin.stockprice.shared.ui.viewModel.FavouriteStocksViewModel
import com.ferelin.stockprice.sharedComposables.components.StocksList
import org.koin.androidx.compose.getViewModel

@Composable
internal fun FavouriteStocksScreenRoute(
    onStockRoute: (StockViewData) -> Unit
) {
    val viewModelWrapper = getViewModel<ViewModelWrapper>()
    val viewModel: FavouriteStocksViewModel = remember { viewModelWrapper.viewModel() }
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