package com.ferelin.stockprice.dataInteractor.local

import android.content.Context
import androidx.core.content.ContextCompat
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.dataInteractor.DataInteractorHelper

interface LocalInteractorHelper {

    suspend fun getCompaniesData(context: Context): LocalInteractorResponse

    suspend fun getSearchesData(context: Context) : LocalInteractorResponse

    suspend fun updateCompany(adaptiveCompany: AdaptiveCompany)

    fun prepareStyles(adaptiveCompany: AdaptiveCompany, index: Int, context: Context) {
        adaptiveCompany.favouriteIconBackground = if (adaptiveCompany.isFavourite) {
            DataInteractorHelper.DRAWABLE_FAVOURITE_ICON_ACTIVE
        } else DataInteractorHelper.DRAWABLE_FAVOURITE_ICON

        adaptiveCompany.holderBackground = if (index % 2 == 0) {
            ContextCompat.getColor(context, DataInteractorHelper.COLOR_HOLDER_FIRST)
        } else ContextCompat.getColor(context, DataInteractorHelper.COLOR_HOLDER_SECOND)

        adaptiveCompany.tickerProfitBackground = List(adaptiveCompany.openPrices.size) {
            if (adaptiveCompany.dayProfitPercents[it][0] == '+') {
                ContextCompat.getColor(context, DataInteractorHelper.COLOR_PROFIT_PLUS)
            } else ContextCompat.getColor(context, DataInteractorHelper.COLOR_PROFIT_MINUS)
        }
    }
}