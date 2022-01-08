package com.ferelin.features.authentication.ui

import com.ferelin.core.ui.view.ControllerConfig
import com.ferelin.core.ui.view.ScreenKey
import com.ferelin.core.ui.view.routing.Event

object LoginScreenKey : ScreenKey() {
  override val controllerConfig = ControllerConfig(
    key = LOGIN_SCREEN_KEY,
    controllerClass = LoginFragment::class.java
  )
}

sealed class LoginRouteEvent : Event() {
  object BackRequested : LoginRouteEvent()
  object UserAuthenticated: LoginRouteEvent()
}

internal val LOGIN_SCREEN_KEY = LoginFragment::class.java.simpleName