package com.ferelin.stockprice.dataInteractor.local

import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest

interface LocalInteractorHelper {

    suspend fun getCompanies(): LocalInteractorResponse

    suspend fun getSearchRequestsHistory(): LocalInteractorResponse

    suspend fun cacheSearchRequestsHistory(requests: List<AdaptiveSearchRequest>)

    suspend fun setFirstTimeLaunchState(state: Boolean)

    suspend fun getFirstTimeLaunchState(): LocalInteractorResponse

    suspend fun cacheCompany(adaptiveCompany: AdaptiveCompany)
}