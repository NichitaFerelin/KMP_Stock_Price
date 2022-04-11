package com.ferelin.stockprice.desktopApp.navigation

import com.ferelin.stockprice.shared.ui.params.AboutParams

internal sealed class Destination(
    val args: Any? = null
) {
    object HomeDestination : Destination()
    object SearchDestination : Destination()
    class AboutDestination(args: AboutParams) : Destination(args)
}