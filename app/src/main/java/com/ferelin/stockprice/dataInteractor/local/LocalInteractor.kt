package com.ferelin.stockprice.dataInteractor.local

import com.ferelin.repository.RepositoryManagerHelper
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.repository.utils.RepositoryResponse
import kotlinx.coroutines.flow.firstOrNull

class LocalInteractor(private val mRepository: RepositoryManagerHelper) : LocalInteractorHelper {

    override suspend fun getCompaniesData(): LocalInteractorResponse {
        val responseCompanies = mRepository.getAllCompanies().firstOrNull()
        return if (responseCompanies is RepositoryResponse.Success) {
            LocalInteractorResponse.Success(responseCompanies.data)
        } else LocalInteractorResponse.Failed()
    }

    override suspend fun getSearchRequestsHistory(): LocalInteractorResponse {
        val responseSearches = mRepository.getSearchesHistory().firstOrNull()
        return if (responseSearches is RepositoryResponse.Success) {
            LocalInteractorResponse.Success(searchesHistory = responseSearches.data)
        } else LocalInteractorResponse.Failed()
    }

    override suspend fun updateCompany(adaptiveCompany: AdaptiveCompany) {
        mRepository.saveCompanyData(adaptiveCompany)
    }

    override suspend fun setSearchRequestsHistory(requests: List<AdaptiveSearchRequest>) {
        mRepository.setSearchesHistory(requests)
    }

    override suspend fun setFirstTimeLaunchState(state: Boolean) {
        mRepository.setFirstTimeLaunchState(state)
    }

    override suspend fun getFirstTimeLaunchState(): LocalInteractorResponse {
        val firstTimeStateResponse = mRepository.getFirstTimeLaunchState().firstOrNull()
        return if (firstTimeStateResponse is RepositoryResponse.Success) {
            LocalInteractorResponse.Success(firstTimeLaunch = firstTimeStateResponse.data)
        } else LocalInteractorResponse.Failed()
    }
}