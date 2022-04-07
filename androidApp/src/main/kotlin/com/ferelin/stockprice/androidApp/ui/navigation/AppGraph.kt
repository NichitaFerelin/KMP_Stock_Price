package com.ferelin.stockprice.androidApp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ferelin.stockprice.androidApp.ui.navigation.Destination.*
import com.ferelin.stockprice.androidApp.ui.screens.AboutScreenRoute
import com.ferelin.stockprice.androidApp.ui.screens.HomeScreenRoute
import com.ferelin.stockprice.androidApp.ui.screens.SearchScreenRoute
import com.ferelin.stockprice.shared.ui.params.AboutParams
import com.ferelin.stockprice.shared.ui.viewData.StockViewData

@Composable
internal fun AppNavigationGraph(
  navHostController: NavHostController
) {
  NavHost(
    navController = navHostController,
    startDestination = HomeDestination.key
  ) {
    composable(route = HomeDestination.key) {
      HomeScreenRoute(
        onSearchRoute = { navHostController.navigate(route = SearchDestination.key) },
        onStockRoute = {
          navHostController.navigate(
            route = AboutDestination.buildNavigationPath(it)
          )
        }
      )
    }
    composable(route = SearchDestination.key) {
      SearchScreenRoute(
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

      AboutScreenRoute(
        params = AboutParams(id, ticker, name),
        onBackRoute = { navHostController.popBackStack() }
      )
    }
  }
}

private fun AboutDestination.buildNavigationPath(stockViewData: StockViewData): String {
  return this.key +
          "/${stockViewData.id.value}" +
          "/${stockViewData.name}" +
          "/${stockViewData.ticker}"
}