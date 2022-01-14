package com.ferelin.stockprice.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.ui.params.AboutParams
import com.ferelin.features.about.AboutRoute
import com.ferelin.features.authentication.LoginRoute
import com.ferelin.features.search.SearchRoute
import com.ferelin.features.settings.SettingsRoute
import com.ferelin.features.splash.LoadingScreen
import com.ferelin.features.stocks.common.CommonRoute
import com.ferelin.stockprice.di.AppComponent
import com.ferelin.stockprice.navigation.Destination.*

@Composable
internal fun AppNavigationGraph(
  navHostController: NavHostController,
  appComponent: AppComponent
) {
  NavHost(
    navController = navHostController,
    startDestination = AboutDestination.key
  ) {
    composable(
      route = AboutDestination.key,
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
        aboutDeps = appComponent,
        aboutParams = AboutParams(CompanyId(id), ticker, name)
      )
    }
    composable(
      route = AuthenticationDestination.key
    ) {
      LoginRoute(loginDeps = appComponent)
    }
    composable(
      route = SearchDestination.key
    ) {
      SearchRoute(searchDeps = appComponent)
    }
    composable(
      route = SettingsDestination.key
    ) {
      SettingsRoute(settingsDeps = appComponent)
    }
    composable(
      route = SplashDestination.key
    ) {
      LoadingScreen()
    }
    composable(
      route = StocksDestination.key
    ) {
      CommonRoute(commonDeps = appComponent)
    }
  }
}