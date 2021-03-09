package com.ferelin.repository

import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utilits.RepositoryResponse
import com.ferelin.repository.utilits.Time
import kotlinx.coroutines.flow.Flow

interface RepositoryManagerHelper {

    fun getAllCompanies(): Flow<RepositoryResponse<List<AdaptiveCompany>>>

    fun openConnection(): Flow<RepositoryResponse<AdaptiveLastPrice>>

    fun subscribeItem(symbol: String)

    fun loadStockCandles(
        company: AdaptiveCompany,
        from: Long = Time.convertMillisForRequest(System.currentTimeMillis() - Time.ONE_YEAR),
        to: Long = Time.convertMillisForRequest(System.currentTimeMillis()),
        resolution: String = "D"
    ): Flow<RepositoryResponse<AdaptiveStockCandles>>

    fun loadCompanyProfile(symbol: String): Flow<RepositoryResponse<AdaptiveCompanyProfile>>

    fun loadStockSymbols(): Flow<RepositoryResponse<AdaptiveStockSymbols>>

    fun loadCompanyNews(symbol: String): Flow<RepositoryResponse<AdaptiveCompanyNews>>

    fun loadCompanyQuote(
        symbol: String,
        position: Int
    ): Flow<RepositoryResponse<AdaptiveCompanyQuote>>

    fun updateCompany(adaptiveCompany: AdaptiveCompany)

    fun getSearchesHistory(): Flow<RepositoryResponse<List<AdaptiveSearchRequest>>>

    suspend fun insertSearch(search: AdaptiveSearchRequest)
}