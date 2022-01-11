package com.ferelin.features.search.ui

import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.ui.view.ControllerConfig
import com.ferelin.core.ui.view.ScreenKey
import com.ferelin.core.ui.view.routing.Event

object SearchScreenKey : ScreenKey() {
  override val controllerConfig = ControllerConfig(
    key = SEARCH_SCREEN_KEY,
    controllerClass = SearchFragment::class.java,
  )
}

sealed class SearchRouteEvent : Event() {
  object BackRequested : SearchRouteEvent()
  class OpenStockInfoRequested(
    val companyId: CompanyId,
    val ticker: String,
    val name: String
  ) : SearchRouteEvent()
}

internal val SEARCH_SCREEN_KEY = SearchFragment::class.java.simpleName