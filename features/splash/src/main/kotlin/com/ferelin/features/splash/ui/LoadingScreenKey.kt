package com.ferelin.features.splash.ui

import com.ferelin.core.ui.view.ControllerConfig
import com.ferelin.core.ui.view.ScreenKey
import com.ferelin.core.ui.view.routing.Event

object LoadingScreenKey : ScreenKey() {
  override val controllerConfig = ControllerConfig(
    key = LOADING_SCREEN_KEY,
    controllerClass = LoadingFragment::class.java,
  )
}

sealed class LoadingRouteEvent : Event() {
  object Loaded : LoadingRouteEvent()
}

internal val LOADING_SCREEN_KEY = LoadingFragment::class.java.simpleName