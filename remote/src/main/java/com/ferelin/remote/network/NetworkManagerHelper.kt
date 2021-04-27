package com.ferelin.remote.network

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.network.companyNews.CompanyNewsResponse
import com.ferelin.remote.network.companyProfile.CompanyProfileResponse
import com.ferelin.remote.network.companyQuote.CompanyQuoteResponse
import com.ferelin.remote.network.stockCandles.StockCandlesResponse
import com.ferelin.remote.network.stockSymbols.StockSymbolResponse
import kotlinx.coroutines.flow.Flow

interface NetworkManagerHelper {

    fun loadStockSymbols(): Flow<BaseResponse<StockSymbolResponse>>

    fun loadCompanyProfile(symbol: String): Flow<BaseResponse<CompanyProfileResponse>>

    fun loadStockCandles(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<BaseResponse<StockCandlesResponse>>

    fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): Flow<BaseResponse<List<CompanyNewsResponse>>>

    fun loadCompanyQuote(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ): Flow<BaseResponse<CompanyQuoteResponse>>
}