package com.ferelin.stockprice.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ferelin.features.home.home.HomeScreenRoute
import com.ferelin.features.stocks.search.SearchScreenRoute
import com.ferelin.features.stocks.stocks.StocksScreenRoute
import com.ferelin.stockprice.navigation.Destination.HomeDestination

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
                onCryptosRoute = { },
                onNewsRoute = { },
                onStocksRoute = {
                    navHostController.navigate(route = Destination.StocksDestination.key)
                },
                onSupportRoute = { }
            )
        }
        composable(route = Destination.StocksDestination.key) {
            StocksScreenRoute(
                onSearchRoute = {
                    navHostController.navigate(route = Destination.SearchDestination.key)
                },
                onStockRoute = { },
                onBackRoute = { }
            )
        }
        composable(route = Destination.SearchDestination.key) {
            SearchScreenRoute(
                onStockRoute = { },
                onBackRoute = { }
            )
        }
    }
}