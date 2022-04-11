package com.ferelin.stockprice.desktopApp.navigation

import androidx.compose.runtime.*
import com.ferelin.stockprice.desktopApp.ui.AboutScreenRoute
import com.ferelin.stockprice.desktopApp.ui.HomeScreenRoute
import com.ferelin.stockprice.desktopApp.ui.SearchScreenRoute
import com.ferelin.stockprice.shared.ui.params.AboutParams

@Composable
internal fun AppNavigationGraph() {
    var screenDestination by remember {
        mutableStateOf<Destination>(Destination.HomeDestination)
    }

    when (screenDestination) {
        is Destination.HomeDestination -> {
            HomeScreenRoute(
                onSearchRoute = { screenDestination = Destination.SearchDestination },
                onStockRoute = { selectedStock ->
                    val aboutParams = AboutParams(
                        companyId = selectedStock.id.value,
                        companyTicker = selectedStock.ticker,
                        companyName = selectedStock.name
                    )
                    screenDestination = Destination.AboutDestination(aboutParams)
                }
            )
        }
        is Destination.SearchDestination -> {
            SearchScreenRoute(
                onBackRoute = { screenDestination = Destination.HomeDestination }
            )
        }
        is Destination.AboutDestination -> {
            AboutScreenRoute(
                params = screenDestination.args as AboutParams,
                onBackRoute = { screenDestination = Destination.HomeDestination }
            )
        }
    }
}