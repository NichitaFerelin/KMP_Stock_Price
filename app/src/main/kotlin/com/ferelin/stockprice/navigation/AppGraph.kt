package com.ferelin.stockprice.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.features.about.about.AboutScreenRoute
import com.ferelin.features.cryptos.cryptos.CryptosScreenRoute
import com.ferelin.features.home.home.HomeScreenRoute
import com.ferelin.features.marketNews.marketNews.MarketNewsScreenRoute
import com.ferelin.features.stocks.search.SearchScreenRoute
import com.ferelin.features.stocks.stocks.StocksScreenRoute
import com.ferelin.stockprice.navigation.Destination.*

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
                onSettingsRoute = { },
                onCryptosRoute = {
                    navHostController.navigate(route = CryptosDestination.key)
                },
                onNewsRoute = {
                    navHostController.navigate(route = MarketNewsDestination.key)
                },
                onStocksRoute = {
                    navHostController.navigate(route = StocksDestination.key)
                },
                onSupportRoute = { }
            )
        }
        composable(route = StocksDestination.key) {
            StocksScreenRoute(
                onSearchRoute = {
                    navHostController.navigate(route = SearchDestination.key)
                },
                onStockRoute = {
                    navHostController.navigate(
                        route = AboutDestination.buildNavigationPath(it)
                    )
                },
                onBackRoute = { navHostController.popBackStack() }
            )
        }
        composable(route = SearchDestination.key) {
            SearchScreenRoute(
                onStockRoute = {
                    navHostController.navigate(
                        route = AboutDestination.buildNavigationPath(it)
                    )
                },
                onBackRoute = { navHostController.popBackStack() }
            )
        }
        composable(
            route = AboutDestination.key + "/{${AboutDestination.ARG_ID}}",
            arguments = listOf(
                navArgument(AboutDestination.ARG_ID) { type = NavType.IntType }
            )
        ) { entry ->
            val args = requireNotNull(entry.arguments)
            val id = args.getInt(AboutDestination.ARG_ID)

            AboutScreenRoute(
                companyId = id,
                onBackRoute = { navHostController.popBackStack() }
            )
        }
        composable(route = CryptosDestination.key) {
            CryptosScreenRoute(
                onBackRoute = { navHostController.popBackStack() }
            )
        }
        composable(route = MarketNewsDestination.key) {
            MarketNewsScreenRoute(
                onBackRoute = { navHostController.popBackStack() }
            )
        }
    }
}

private fun AboutDestination.buildNavigationPath(companyId: CompanyId): String {
    return this.key + "/${companyId.value}"
}