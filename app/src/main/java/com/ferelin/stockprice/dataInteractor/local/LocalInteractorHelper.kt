package com.ferelin.stockprice.dataInteractor.local

import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest

interface LocalInteractorHelper {

    suspend fun getCompaniesData(): LocalInteractorResponse

    suspend fun getSearchRequestsHistory(): LocalInteractorResponse

    suspend fun setSearchRequestsHistory(requests: List<AdaptiveSearchRequest>)

    suspend fun setFirstTimeLaunchState(state: Boolean)

    suspend fun getFirstTimeLaunchState(): LocalInteractorResponse

    suspend fun updateCompany(adaptiveCompany: AdaptiveCompany)
}