package com.ferelin.remote.network.stockSymbols

import com.squareup.moshi.Json

class StockSymbolResponse(
    @Json(name = "symbol") val stockSymbols: List<String>
)
