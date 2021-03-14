package com.ferelin.stockprice.dataInteractor

import android.content.Context
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import kotlinx.coroutines.flow.Flow

interface DataInteractorHelper {

    suspend fun prepareData(context: Context)

    suspend fun loadStockCandles(symbol: String): Flow<AdaptiveCompany>

    suspend fun loadCompanyNews(symbol: String): Flow<AdaptiveCompany>

    suspend fun loadCompanyQuote(symbol: String, position: Int): Flow<AdaptiveCompany>

    suspend fun openConnection(): Flow<AdaptiveCompany>

    suspend fun addCompanyToFavourite(adaptiveCompany: AdaptiveCompany)

    suspend fun addCompanyToFavourite(symbol: String)

    suspend fun removeCompanyFromFavourite(adaptiveCompany: AdaptiveCompany)

    suspend fun removeCompanyFromFavourite(symbol: String)

    suspend fun onNewSearch(searchText: String)

    fun getCompany(symbol: String) : AdaptiveCompany?
}