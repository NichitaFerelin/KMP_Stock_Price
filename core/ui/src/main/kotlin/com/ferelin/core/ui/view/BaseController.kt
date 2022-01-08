package com.ferelin.core.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ferelin.core.ui.view.routing.RouteEvents

abstract class BaseController : Fragment()

abstract class ScreenKey {
  protected abstract val controllerConfig: ControllerConfig

  fun createController(args: Bundle? = null): BaseController {
    return controllerConfig.controllerClass.newInstance()
      .apply { arguments = args }
  }
}

data class ControllerConfig(
  val name: String,
  val controllerClass: Class<out BaseController>,
  val routeEvents: RouteEvents
)