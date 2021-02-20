package com.ferelin.remote.network

import com.ferelin.remote.network.companyProfile.CompanyProfileResponse
import com.ferelin.remote.network.stockCandles.StockCandlesResponse
import com.ferelin.remote.network.stockSymbol.StockSymbolResponse
import kotlinx.coroutines.flow.Flow


interface NetworkManagerHelper {

    fun loadStockSymbols(): Flow<List<StockSymbolResponse>>
    fun loadCompanyProfile(symbol: String): Flow<CompanyProfileResponse>
    fun loadStockCandle(symbol: String, from: Double, to: Double): Flow<StockCandlesResponse>
    fun checkUpdates(previousLoadedSymbols: Collection<String>): Flow<List<StockSymbolResponse>>
}