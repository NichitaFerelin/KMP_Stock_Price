package com.ferelin.features.home.favourite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ferelin.core.ui.components.StocksList
import com.ferelin.core.ui.viewData.StockViewData
import org.koin.androidx.compose.getViewModel

@Composable
fun FavouriteStocksRoute(
  onStockRoute: (StockViewData) -> Unit
) {

}

@Composable
private fun FavouriteStocksScreen(
  onFavouriteIconClick: (StockViewData) -> Unit,
  onStockClick: (StockViewData) -> Unit
) {
}