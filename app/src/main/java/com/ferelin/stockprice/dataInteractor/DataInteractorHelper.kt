package com.ferelin.stockprice.dataInteractor

import android.content.Context
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import kotlinx.coroutines.flow.Flow

interface DataInteractorHelper {

    suspend fun prepareData(context: Context)

    suspend fun loadStockCandles(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<AdaptiveCompany>

    suspend fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): Flow<AdaptiveCompany>

    suspend fun loadCompanyQuote(symbol: String, position: Int): Flow<AdaptiveCompany>

    suspend fun openConnection(): Flow<AdaptiveCompany>

    suspend fun subscribeItem(symbol: String, openPrice: Double)

    suspend fun addCompanyToFavourite(adaptiveCompany: AdaptiveCompany)

    suspend fun removeCompanyFromFavourite(adaptiveCompany: AdaptiveCompany)

    suspend fun onNewSearch(searchText: String)
}