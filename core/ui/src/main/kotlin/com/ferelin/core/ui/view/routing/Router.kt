package com.ferelin.core.ui.view.routing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.ferelin.core.ui.view.ScreenKey
import javax.inject.Inject

interface Router {
  fun push(screenKey: ScreenKey, args: Bundle? = null)
  fun pop()
}

interface RouterHost {
  var host: AppCompatActivity?
  val containerViewId: Int
}

interface Coordinator {
  fun onEvent(event: Event)
}

abstract class Event

internal class AppRouter @Inject constructor(
  private val routerHost: RouterHost
) : Router {
  override fun push(screenKey: ScreenKey, args: Bundle?) {
    routerHost.host?.supportFragmentManager?.commit {
      replace(routerHost.containerViewId, screenKey.createController(args))
    }
  }

  override fun pop() {
    routerHost.host?.supportFragmentManager?.popBackStack()
  }
}