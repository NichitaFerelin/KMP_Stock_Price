package com.ferelin.core.ui.view.routing

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.ferelin.core.ui.view.ScreenKey
import javax.inject.Inject

interface Router {
  fun push(screenKey: ScreenKey)
  fun pop()
  fun popTo(screenKey: ScreenKey)
}

interface RouterHost {
  var routerHost: AppCompatActivity?
  val containerViewId: Int
}

interface Coordinator {
  fun onEvent(event: Event)
}

abstract class RouteEvents
abstract class Event(val parentClass: Class<out RouteEvents>)

internal class AppRouter @Inject constructor(
  private val routerHost: RouterHost
) : Router {
  override fun push(screenKey: ScreenKey) {
    routerHost.routerHost?.supportFragmentManager?.commit {
      replace(routerHost.containerViewId, screenKey.createController())
    }
  }

  override fun pop() {
  }

  override fun popTo(screenKey: ScreenKey) {
  }
}