package com.ferelin.navigation

import com.ferelin.core.ui.view.routing.Coordinator
import com.ferelin.core.ui.view.routing.Event
import com.ferelin.core.ui.view.routing.Router
import com.ferelin.features.search.ui.SearchRouteEvents
import com.ferelin.features.search.ui.SearchScreenKey
import javax.inject.Inject

internal class AppCoordinator @Inject constructor(
  private val router: Router
) : Coordinator {
  override fun onEvent(event: Event) {
    when (event.parentClass) {
      SearchRouteEvents::class.java -> {
        when (event) {
          is SearchRouteEvents.OpenEvent -> {
            router.push(SearchScreenKey)
          }
        }
      }
    }
  }
}