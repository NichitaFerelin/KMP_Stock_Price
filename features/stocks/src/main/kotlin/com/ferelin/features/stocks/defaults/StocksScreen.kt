package com.ferelin.features.stocks.defaults

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ferelin.core.ui.component.StockItem
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.core.ui.viewData.StockViewData

@Composable
internal fun StocksRoute(stocksViewModel: StocksViewModel) {
  val uiState by stocksViewModel.uiState.collectAsState()

  StocksScreen(
    stocksStateUi = uiState,
    onFavouriteIconClick = stocksViewModel::onFavouriteIconClick
  )
}

@Composable
internal fun StocksScreen(
  stocksStateUi: StocksStateUi,
  onFavouriteIconClick: (StockViewData) -> Unit
) {
  LazyColumn {
    items(
      items = stocksStateUi.companies
    ) { stockViewData ->
      StockItem(
        backgroundColor = AppTheme.colors.backgroundPrimary,
        iconUrl = stockViewData.logoUrl,
        ticker = stockViewData.ticker,
        name = stockViewData.name,
        isFavourite = stockViewData.isFavourite,
        onFavouriteIconClick = { onFavouriteIconClick(stockViewData) },
        onClick = { /**/ }
      )
    }
  }
}