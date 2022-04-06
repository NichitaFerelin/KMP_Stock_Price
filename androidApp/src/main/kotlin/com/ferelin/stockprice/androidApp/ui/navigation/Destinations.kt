package com.ferelin.stockprice.androidApp.ui.navigation

internal sealed class Destination(val key: String) {

  object SplashDestination : Destination("splash")
  object HomeDestination : Destination("home")
  object SearchDestination : Destination("search")
  object SettingsDestination : Destination("settings")
  object AuthenticationDestination : Destination("authentication")

  object AboutDestination : Destination("about") {
    const val ARG_ID = "id"
    const val ARG_NAME = "name"
    const val ARG_TICKER = "ticker"
  }
}