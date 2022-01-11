package com.ferelin.features.stocks.ui.defaults

import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.ui.view.ControllerConfig
import com.ferelin.core.ui.view.ScreenKey
import com.ferelin.core.ui.view.routing.Event

object StocksScreenKey : ScreenKey() {
  override val controllerConfig = ControllerConfig(
    key = STOCKS_SCREEN_KEY,
    controllerClass = StocksFragment::class.java,
  )
}

sealed class StocksRouteEvent : Event() {
  class OpenStockInfoRequested(
    val companyId: CompanyId,
    val ticker: String,
    val name: String
  ) : StocksRouteEvent()
}

internal val STOCKS_SCREEN_KEY = StocksFragment::class.java.simpleName