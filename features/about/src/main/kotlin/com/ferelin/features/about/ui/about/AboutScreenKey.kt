package com.ferelin.features.about.ui.about

import com.ferelin.core.ui.view.ControllerConfig
import com.ferelin.core.ui.view.ScreenKey
import com.ferelin.core.ui.view.routing.Event

object AboutScreenKey : ScreenKey() {
  override val controllerConfig = ControllerConfig(
    key = ABOUT_SCREEN_KEY,
    controllerClass = AboutFragment::class.java
  )
}

sealed class AboutScreenEvent : Event() {
  object BackRequested : AboutScreenEvent()
}

internal val ABOUT_SCREEN_KEY = AboutFragment::class.java.simpleName