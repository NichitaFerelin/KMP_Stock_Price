package com.ferelin.features.stocks.ui.common

import com.ferelin.core.ui.view.ControllerConfig
import com.ferelin.core.ui.view.ScreenKey
import com.ferelin.core.ui.view.routing.Event

object CommonScreenKey : ScreenKey() {
  override val controllerConfig = ControllerConfig(
    key = COMMON_SCREEN_KEY,
    controllerClass = CommonFragment::class.java,
  )
}

sealed class CommonRouteEvent : Event() {
  object SettingsRequested : CommonRouteEvent()
  object SearchRequested : CommonRouteEvent()
}

internal val COMMON_SCREEN_KEY = CommonFragment::class.java.simpleName