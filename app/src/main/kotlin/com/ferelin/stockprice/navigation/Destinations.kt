package com.ferelin.stockprice.navigation

internal sealed class Destination(val key: String) {
  object AboutDestination : Destination("about") {
    const val ARG_ID = "id"
    const val ARG_NAME = "name"
    const val ARG_TICKER = "ticker"
  }

  object SplashDestination : Destination("splash")
  object AuthenticationDestination : Destination("authentication")
  object SearchDestination : Destination("search")
  object SettingsDestination : Destination("settings")
  object OverviewDestination : Destination("overview")
}