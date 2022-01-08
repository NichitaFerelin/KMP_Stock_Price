package com.ferelin.features.about.ui.profile

import com.ferelin.core.ui.view.ControllerConfig
import com.ferelin.core.ui.view.ScreenKey

object ProfileScreenKey : ScreenKey() {
  override val controllerConfig = ControllerConfig(
    key = PROFILE_SCREEN_KEY,
    controllerClass = ProfileFragment::class.java,
  )
}

internal val PROFILE_SCREEN_KEY = ProfileFragment::class.java.simpleName