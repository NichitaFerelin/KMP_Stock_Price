package com.ferelin.features.stocks.ui.favourites

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.ui.view.routing.Coordinator
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.core.ui.viewData.utils.StockStyleProvider
import com.ferelin.core.ui.viewModel.BaseStocksViewModel
import com.ferelin.features.stocks.ui.defaults.StocksRouteEvent
import javax.inject.Inject

internal class FavouriteStocksViewModel @Inject constructor(
  private val companyUseCase: CompanyUseCase,
  private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
  private val stockStyleProvider: StockStyleProvider,
  private val coordinator: Coordinator,
  dispatchersProvider: DispatchersProvider
) : BaseStocksViewModel(
  companyUseCase,
  favouriteCompanyUseCase,
  stockStyleProvider,
  dispatchersProvider
) {
  override fun onStockClick(stockViewData: StockViewData) {
    coordinator.onEvent(
      FavouriteStocksRouteEvent.OpenStockInfoRequested(
        companyId = stockViewData.id,
        ticker = stockViewData.ticker,
        name = stockViewData.name
      )
    )
  }
}