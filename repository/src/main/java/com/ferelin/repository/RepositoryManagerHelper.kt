package com.ferelin.repository

import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utilits.RepositoryResponse
import com.ferelin.repository.utilits.TimeMillis
import kotlinx.coroutines.flow.Flow

interface RepositoryManagerHelper {

    fun getAllCompanies(): Flow<RepositoryResponse<List<AdaptiveCompany>>>

    fun openConnection(
        dataToSubscribe: Collection<String>
    ): Flow<RepositoryResponse<AdaptiveLastPrice>>

    fun loadStockCandles(
        company: AdaptiveCompany,
        from: Long = TimeMillis.convertForRequest(System.currentTimeMillis() - TimeMillis.ONE_YEAR),
        to: Long = TimeMillis.convertForRequest(System.currentTimeMillis()),
        resolution: String = "D"
    ): Flow<RepositoryResponse<AdaptiveStockCandle>>

    fun loadCompanyProfile(symbol: String): Flow<RepositoryResponse<AdaptiveCompanyProfile>>

    fun loadStockSymbols(): Flow<RepositoryResponse<AdaptiveStockSymbols>>

    fun updateCompany(adaptiveCompany: AdaptiveCompany)
}