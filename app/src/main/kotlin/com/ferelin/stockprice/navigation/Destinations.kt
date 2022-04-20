package com.ferelin.stockprice.navigation

internal sealed class Destination(val key: String) {
    object SplashDestination: Destination("splash")
    object HomeDestination : Destination("home")
    object StocksDestination : Destination("stocks")
    object SearchDestination : Destination("search")
    object AboutDestination : Destination("about") {
        const val ARG_ID = "id"
    }
    object CryptosDestination : Destination("cryptos")
    object MarketNewsDestination : Destination("market-news")
}