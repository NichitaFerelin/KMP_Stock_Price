package com.ferelin.features.settings.ui

import com.ferelin.core.ui.view.ControllerConfig
import com.ferelin.core.ui.view.ScreenKey
import com.ferelin.core.ui.view.routing.Event

object SettingsScreenKey : ScreenKey() {
  override val controllerConfig = ControllerConfig(
    key = SETTINGS_SCREEN_KEY,
    controllerClass = SettingsFragment::class.java,
  )
}

sealed class SettingsRouteEvent : Event() {
  object BackRequested : SettingsRouteEvent()
  object AuthenticationRequested : SettingsRouteEvent()
}

internal val SETTINGS_SCREEN_KEY = SettingsFragment::class.java.simpleName