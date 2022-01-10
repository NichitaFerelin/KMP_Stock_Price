package com.ferelin.navigation

import androidx.core.os.bundleOf
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.ui.params.AboutParams
import com.ferelin.core.ui.view.routing.Coordinator
import com.ferelin.core.ui.view.routing.Event
import com.ferelin.core.ui.view.routing.Router
import com.ferelin.features.about.ui.about.AboutScreenEvent
import com.ferelin.features.about.ui.about.AboutScreenKey
import com.ferelin.features.authentication.ui.LoginRouteEvent
import com.ferelin.features.search.ui.SearchRouteEvent
import com.ferelin.features.splash.ui.LoadingRouteEvent
import com.ferelin.features.splash.ui.LoadingScreenKey
import com.ferelin.features.stocks.ui.common.CommonScreenKey
import com.ferelin.features.stocks.ui.defaults.StocksRouteEvent
import com.ferelin.features.stocks.ui.favourites.FavouriteStocksRouteEvent
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AppCoordinator @Inject constructor(
  private val router: Router
) : Coordinator {
  override fun initialRoute() {
    router.push(LoadingScreenKey)
  }

  override fun onEvent(event: Event) {
    Timber.d("On route event $event")
    when (event) {
      is LoadingRouteEvent.Loaded -> router.push(CommonScreenKey)
      is LoginRouteEvent -> {
        when (event) {
          is LoginRouteEvent.BackRequested -> router.pop()
          is LoginRouteEvent.UserAuthenticated -> router.pop()
        }
      }
      is SearchRouteEvent -> {
        when (event) {
          is SearchRouteEvent.OpenStockInfoRequested -> toAboutScreen(event.companyId, event.ticker, event.name)
          is SearchRouteEvent.BackRequested -> router.pop()
        }
      }
      is StocksRouteEvent.OpenStockInfoRequested -> toAboutScreen(event.companyId, event.ticker, event.name)
      is FavouriteStocksRouteEvent.OpenStockInfoRequested -> toAboutScreen(event.companyId, event.ticker, event.name)
      is AboutScreenEvent.BackRequested -> router.pop()
      else -> error("Router event ignored $event")
    }
  }

  private fun toAboutScreen(companyId: CompanyId, ticker: String, name: String) {
    router.push(
      screenKey = AboutScreenKey,
      args = bundleOf(
        AboutScreenKey.controllerConfig.key to AboutParams(companyId, ticker, name)
      )
    )
  }
}