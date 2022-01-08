package com.ferelin.features.about.ui.news

import com.ferelin.core.ui.view.ControllerConfig
import com.ferelin.core.ui.view.ScreenKey

object NewsScreenKey : ScreenKey() {
  override val controllerConfig = ControllerConfig(
    key = NEWS_SCREEN_KEY,
    controllerClass = NewsFragment::class.java,
  )
}

internal val NEWS_SCREEN_KEY = NewsFragment::class.java.simpleName