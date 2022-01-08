package com.ferelin.features.about.ui.chart

import com.ferelin.core.ui.view.ControllerConfig
import com.ferelin.core.ui.view.ScreenKey

object ChartScreenKey : ScreenKey() {
  override val controllerConfig = ControllerConfig(
    key = CHART_SCREEN_KEY,
    controllerClass = ChartFragment::class.java
  )
}

internal val CHART_SCREEN_KEY = ChartFragment::class.java.simpleName