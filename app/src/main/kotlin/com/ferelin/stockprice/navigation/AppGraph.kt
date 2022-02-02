package com.ferelin.stockprice.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.ui.params.AboutParams
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.features.about.AboutRoute
import com.ferelin.features.authentication.LoginRoute
import com.ferelin.features.search.SearchRoute
import com.ferelin.features.settings.SettingsRoute
import com.ferelin.features.splash.LoadingScreen
import com.ferelin.features.stocks.overview.OverviewRoute
import com.ferelin.stockprice.di.AppComponent
import com.ferelin.stockprice.navigation.Destination.*

@Composable
internal fun AppNavigationGraph(
  navHostController: NavHostController,
  appComponent: AppComponent
) {
  NavHost(
    navController = navHostController,
    startDestination = SplashDestination.key
  ) {
    composable(route = SplashDestination.key) {
      LoadingScreen {
        navHostController.navigate(OverviewDestination.key)
      }
    }
    composable(route = OverviewDestination.key) {
      OverviewRoute(
        deps = appComponent,
        onSettingsRoute = { navHostController.navigate(route = SettingsDestination.key) },
        onSearchRoute = { navHostController.navigate(route = SearchDestination.key) },
        onStockRoute = {
          navHostController.navigate(
            route = AboutDestination.buildNavigationPath(it)
          )
        }
      )
    }
    composable(route = SettingsDestination.key) {
      SettingsRoute(
        deps = appComponent,
        onLogInRoute = { navHostController.navigate(route = AuthenticationDestination.key) },
        onBackRoute = { navHostController.popBackStack() }
      )
    }
    composable(route = AuthenticationDestination.key) {
      LoginRoute(
        deps = appComponent,
        onBackRoute = { navHostController.popBackStack() }
      )
    }
    composable(route = SearchDestination.key) {
      SearchRoute(
        deps = appComponent,
        onBackRoute = { navHostController.popBackStack() },
        onStockRoute = {
          navHostController.navigate(
            route = AboutDestination.buildNavigationPath(it)
          )
        }
      )
    }
    composable(
      route = AboutDestination.key +
        "/{${AboutDestination.ARG_ID}}" +
        "/{${AboutDestination.ARG_NAME}}" +
        "/{${AboutDestination.ARG_TICKER}}",
      arguments = listOf(
        navArgument(AboutDestination.ARG_ID) { type = NavType.IntType },
        navArgument(AboutDestination.ARG_NAME) { type = NavType.StringType },
        navArgument(AboutDestination.ARG_TICKER) { type = NavType.StringType },
      )
    ) { entry ->
      val args = requireNotNull(entry.arguments)
      val id = args.getInt(AboutDestination.ARG_ID)
      val name = requireNotNull(args.getString(AboutDestination.ARG_NAME))
      val ticker = requireNotNull(args.getString(AboutDestination.ARG_TICKER))

      AboutRoute(
        deps = appComponent,
        params = AboutParams(CompanyId(id), ticker, name),
        onBackRoute = { navHostController.popBackStack() }
      )
    }
  }
}

private fun AboutDestination.buildNavigationPath(stockViewData: StockViewData): String {
  return AboutDestination.key +
    "/${stockViewData.id.value}" +
    "/${stockViewData.name}" +
    "/${stockViewData.ticker}"
}