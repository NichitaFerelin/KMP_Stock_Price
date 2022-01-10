package com.ferelin.features.stocks.ui.favourites

import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.ui.view.ControllerConfig
import com.ferelin.core.ui.view.ScreenKey
import com.ferelin.core.ui.view.routing.Event
import com.ferelin.features.stocks.ui.defaults.STOCKS_SCREEN_KEY
import com.ferelin.features.stocks.ui.defaults.StocksFragment

object FavouriteStocksScreenKey : ScreenKey() {
  override val controllerConfig = ControllerConfig(
    key = FAVOURITE_STOCKS_SCREEN_KEY,
    controllerClass = FavouriteStocksFragment::class.java,
  )
}

sealed class FavouriteStocksRouteEvent : Event() {
  class OpenStockInfoRequested(
    val companyId: CompanyId,
    val ticker: String,
    val name: String
  ) : FavouriteStocksRouteEvent()
}

internal val FAVOURITE_STOCKS_SCREEN_KEY = FavouriteStocksFragment::class.java.simpleName