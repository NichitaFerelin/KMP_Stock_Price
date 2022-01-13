package com.ferelin.stockprice.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.ui.params.AboutParams
import com.ferelin.features.about.about.AboutRoute
import com.ferelin.features.about.about.AboutViewModel
import com.ferelin.features.authentication.LoginRoute
import com.ferelin.features.authentication.LoginViewModel
import com.ferelin.features.search.SearchRoute
import com.ferelin.features.search.SearchViewModel
import com.ferelin.features.settings.SettingsRoute
import com.ferelin.features.settings.SettingsViewModel
import com.ferelin.features.splash.LoadingScreen
import com.ferelin.features.stocks.common.CommonRoute
import com.ferelin.features.stocks.common.CommonViewModel
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
      val component = AboutDestination.component(
        appComponent = appComponent,
        aboutParams = AboutParams(CompanyId(id), ticker, name)
      )
      val viewModel: AboutViewModel = viewModel(factory = component.viewModelFactory())
      AboutRoute(aboutViewModel = viewModel)
    }
    composable(
      route = AuthenticationDestination.key
    ) {
      val component = AuthenticationDestination.component(appComponent)
      val viewModel: LoginViewModel = viewModel(factory = component.viewModelFactory())
      LoginRoute(loginViewModel = viewModel)
    }
    composable(
      route = SearchDestination.key
    ) {
      val component = SearchDestination.component(appComponent)
      val viewModeL: SearchViewModel = viewModel(factory = component.viewModelFactory())
      SearchRoute(searchViewModel = viewModeL)
    }
    composable(
      route = SettingsDestination.key
    ) {
      val component = SettingsDestination.component(appComponent)
      val viewModel: SettingsViewModel = viewModel(factory = component.viewModelFactory())
      SettingsRoute(settingsViewModel = viewModel)
    }
    composable(
      route = SplashDestination.key
    ) {
      LoadingScreen()
    }
    composable(
      route = StocksDestination.key
    ) {
      val component = StocksDestination.component(appComponent)
      val viewModel: CommonViewModel = viewModel(factory = component.viewModelFactory())
      CommonRoute(commonViewModel = viewModel)
    }
  }
}