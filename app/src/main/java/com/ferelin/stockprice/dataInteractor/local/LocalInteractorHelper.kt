package com.ferelin.stockprice.dataInteractor.local

import android.content.Context
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest

interface LocalInteractorHelper {

    suspend fun getCompaniesData(context: Context): LocalInteractorResponse

    suspend fun getSearchesData(context: Context): LocalInteractorResponse

    suspend fun setSearchesData(requests: List<AdaptiveSearchRequest>)

    suspend fun updateCompany(adaptiveCompany: AdaptiveCompany)
}