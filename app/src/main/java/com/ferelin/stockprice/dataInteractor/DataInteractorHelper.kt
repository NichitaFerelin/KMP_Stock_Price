package com.ferelin.stockprice.dataInteractor

import android.content.Context
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveLastPrice
import com.ferelin.repository.adaptiveModels.AdaptiveStockCandle
import com.ferelin.stockprice.R
import kotlinx.coroutines.flow.Flow

interface DataInteractorHelper {

    suspend fun prepareCompaniesData(context: Context)

    suspend fun prepareSearchesHistory(context: Context)

    suspend fun addCompanyToFavourite(adaptiveCompany: AdaptiveCompany)

    suspend fun subscribeItem(symbol: String)

    suspend fun removeCompanyFromFavourite(adaptiveCompany: AdaptiveCompany)

    suspend fun loadStockCandles(item: AdaptiveCompany, position: Int): Flow<AdaptiveStockCandle>

    suspend fun openConnection(): Flow<AdaptiveLastPrice>

    companion object {
        const val DRAWABLE_FAVOURITE_ICON_ACTIVE = R.drawable.ic_favourite_active
        const val DRAWABLE_FAVOURITE_ICON = R.drawable.ic_favourite
        const val COLOR_PROFIT_PLUS = R.color.green
        const val COLOR_PROFIT_MINUS = R.color.red
        const val COLOR_HOLDER_FIRST = R.color.white
        const val COLOR_HOLDER_SECOND = R.color.whiteDark
    }
}