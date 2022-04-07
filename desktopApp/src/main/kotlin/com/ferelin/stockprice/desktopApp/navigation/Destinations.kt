package com.ferelin.stockprice.desktopApp.navigation

import com.ferelin.stockprice.shared.ui.params.AboutParams

internal sealed class Destination(
  val key: String,
  val args: Any? = null
) {
  object HomeDestination : Destination("home")
  object SearchDestination : Destination("search")
  class AboutDestination(args: AboutParams) : Destination("about", args)
}