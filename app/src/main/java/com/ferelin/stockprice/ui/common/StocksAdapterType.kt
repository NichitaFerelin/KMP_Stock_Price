package com.ferelin.stockprice.ui.common

sealed class StocksAdapterType {
    object Default : StocksAdapterType()
    object Favourite : StocksAdapterType()
}