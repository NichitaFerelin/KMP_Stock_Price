package com.ferelin.stockprice.dataInteractor

import android.content.Context
import com.ferelin.repository.adaptiveModels.*
import kotlinx.coroutines.flow.Flow

interface DataInteractorHelper {

    suspend fun prepareData(context: Context)

    suspend fun addCompanyToFavourite(adaptiveCompany: AdaptiveCompany)

    suspend fun subscribeItem(symbol: String)

    suspend fun removeCompanyFromFavourite(adaptiveCompany: AdaptiveCompany)

    suspend fun loadStockCandles(item: AdaptiveCompany, position: Int): Flow<AdaptiveStockCandles>

    suspend fun loadCompanyNews(symbol: String): Flow<AdaptiveCompanyNews>

    suspend fun loadCompanyQuote(symbol: String, position: Int): Flow<AdaptiveCompanyQuote>

    suspend fun openConnection(): Flow<AdaptiveLastPrice>
}