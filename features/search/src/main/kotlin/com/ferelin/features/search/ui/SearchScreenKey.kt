package com.ferelin.features.search.ui

import com.ferelin.core.ui.view.routing.Event
import com.ferelin.core.ui.view.routing.RouteEvents
import com.ferelin.core.ui.view.ControllerConfig
import com.ferelin.core.ui.view.ScreenKey

object SearchScreenKey : ScreenKey() {
  override val controllerConfig = ControllerConfig(
    name = SearchFragment::class.java.simpleName,
    controllerClass = SearchFragment::class.java,
    routeEvents = SearchRouteEvents
  )
}

object SearchRouteEvents : RouteEvents() {
  object OpenEvent : Event(SearchRouteEvents::class.java)
}