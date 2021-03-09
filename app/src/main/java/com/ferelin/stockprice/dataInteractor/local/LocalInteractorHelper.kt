package com.ferelin.stockprice.dataInteractor.local

import android.content.Context
import com.ferelin.repository.adaptiveModels.AdaptiveCompany

interface LocalInteractorHelper {

    suspend fun getCompaniesData(context: Context): LocalInteractorResponse

    suspend fun getSearchesData(context: Context): LocalInteractorResponse

    suspend fun updateCompany(adaptiveCompany: AdaptiveCompany)
}