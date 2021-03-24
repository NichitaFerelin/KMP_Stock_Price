package com.ferelin.stockprice.dataInteractor.local

import android.content.Context
import com.ferelin.repository.RepositoryManagerHelper
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.repository.utils.RepositoryResponse
import kotlinx.coroutines.flow.first

class LocalInteractor(private val mRepository: RepositoryManagerHelper) : LocalInteractorHelper {

    override suspend fun getCompaniesData(context: Context): LocalInteractorResponse {
        val responseCompanies = mRepository.getAllCompanies().first()
        return if (responseCompanies is RepositoryResponse.Success) {
            LocalInteractorResponse.Success(responseCompanies.data)
        } else LocalInteractorResponse.Failed()
    }

    override suspend fun getSearchRequestsHistory(context: Context): LocalInteractorResponse {
        val responseSearches = mRepository.getSearchesHistory().first()
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
}