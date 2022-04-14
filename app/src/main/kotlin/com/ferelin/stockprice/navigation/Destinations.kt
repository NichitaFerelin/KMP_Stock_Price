package com.ferelin.stockprice.navigation

internal sealed class Destination(val key: String) {
    object HomeDestination : Destination("home")
}