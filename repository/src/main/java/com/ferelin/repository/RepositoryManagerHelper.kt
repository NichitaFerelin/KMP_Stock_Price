package com.ferelin.repository

import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.repository.utils.Time
import kotlinx.coroutines.flow.Flow

interface RepositoryManagerHelper {

    fun loadStockCandles(
        symbol: String,
        from: Long = Time.convertMillisForRequest(System.currentTimeMillis() - Time.ONE_YEAR),
        to: Long = Time.convertMillisForRequest(System.currentTimeMillis()),
        resolution: String = "D"
    ): Flow<RepositoryResponse<AdaptiveCompanyHistory>>

    fun loadCompanyProfile(symbol: String): Flow<RepositoryResponse<AdaptiveCompanyProfile>>

    fun loadStockSymbols(): Flow<RepositoryResponse<AdaptiveStocksSymbols>>

    fun loadCompanyNews(
        symbol: String,
        from: String = Time.getYearAgoDateForRequest(),
        to: String = Time.getCurrentDateForRequest()
    ): Flow<RepositoryResponse<AdaptiveCompanyNews>>

    fun openConnection(): Flow<RepositoryResponse<AdaptiveWebSocketPrice>>

    fun loadCompanyQuote(
        symbol: String,
        position: Int
    ): Flow<RepositoryResponse<AdaptiveCompanyDayData>>

    fun saveCompanyData(adaptiveCompany: AdaptiveCompany)

    fun getAllCompanies(): Flow<RepositoryResponse<List<AdaptiveCompany>>>

    fun subscribeItem(symbol: String, openPrice: Double)

    fun unsubscribeItem(symbol: String)

    fun getSearchesHistory(): Flow<RepositoryResponse<List<AdaptiveSearchRequest>>>

    suspend fun setSearchesHistory(requests: List<AdaptiveSearchRequest>)
}