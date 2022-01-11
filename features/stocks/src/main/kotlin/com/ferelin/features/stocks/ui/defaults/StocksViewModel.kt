package com.ferelin.features.stocks.ui.defaults

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.ui.view.routing.Coordinator
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.core.ui.viewData.utils.StockStyleProvider
import com.ferelin.core.ui.viewModel.BaseStocksViewModel
import javax.inject.Inject

internal class StocksViewModel @Inject constructor(
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
      StocksRouteEvent.OpenStockInfoRequested(
        companyId = stockViewData.id,
        ticker = stockViewData.ticker,
        name = stockViewData.name
      )
    )
  }
}