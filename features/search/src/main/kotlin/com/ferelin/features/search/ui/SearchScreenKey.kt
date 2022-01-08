package com.ferelin.features.search.ui

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
  object OpenStockInfoRequested : SearchRouteEvent()
}

internal val SEARCH_SCREEN_KEY = SearchFragment::class.java.simpleName