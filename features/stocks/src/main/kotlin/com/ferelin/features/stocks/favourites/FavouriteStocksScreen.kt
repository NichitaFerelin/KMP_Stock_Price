package com.ferelin.features.stocks.favourites

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ferelin.core.ui.component.StockItem
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.core.ui.viewData.StockViewData

@Composable
internal fun FavouriteStocksRoute(favouriteStocksViewModel: FavouriteStocksViewModel) {
  val uiState by favouriteStocksViewModel.uiState.collectAsState()

  StocksScreen(
    favouriteStocksStateUi = uiState,
    onFavouriteIconClick = favouriteStocksViewModel::onFavouriteIconClick
  )
}

@Composable
internal fun StocksScreen(
  favouriteStocksStateUi: FavouriteStocksStateUi,
  onFavouriteIconClick: (StockViewData) -> Unit
) {
  LazyColumn {
    items(
      items = favouriteStocksStateUi.companies
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