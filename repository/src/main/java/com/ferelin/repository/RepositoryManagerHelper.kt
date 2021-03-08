package com.ferelin.repository

import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utilits.RepositoryResponse
import com.ferelin.repository.utilits.TimeMillis
import kotlinx.coroutines.flow.Flow

interface RepositoryManagerHelper {

    fun getAllCompanies(): Flow<RepositoryResponse<List<AdaptiveCompany>>>

    fun openConnection(): Flow<RepositoryResponse<AdaptiveLastPrice>>

    fun subscribeItem(symbol: String)

    fun loadStockCandles(
        company: AdaptiveCompany,
        position: Int,
        from: Long = TimeMillis.convertForRequest(System.currentTimeMillis() - TimeMillis.ONE_YEAR),
        to: Long = TimeMillis.convertForRequest(System.currentTimeMillis()),
        resolution: String = "D"
    ): Flow<RepositoryResponse<AdaptiveStockCandle>>

    fun loadCompanyProfile(symbol: String): Flow<RepositoryResponse<AdaptiveCompanyProfile>>

    fun loadStockSymbols(): Flow<RepositoryResponse<AdaptiveStockSymbols>>

    fun updateCompany(adaptiveCompany: AdaptiveCompany)

    fun getSearchesHistory() : Flow<RepositoryResponse<List<AdaptiveSearch>>>

    fun getPopularSearches() : List<AdaptiveSearch>

    fun insertSearch(search: AdaptiveSearch)
}