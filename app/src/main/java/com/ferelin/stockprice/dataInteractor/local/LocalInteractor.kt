package com.ferelin.stockprice.dataInteractor.local

import android.content.Context
import com.ferelin.repository.RepositoryManagerHelper
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.utilits.RepositoryResponse
import kotlinx.coroutines.flow.first

class LocalInteractor(private val mRepository: RepositoryManagerHelper) : LocalInteractorHelper {

    override suspend fun getCompaniesData(context: Context): LocalInteractorResponse {
        val responseCompanies = mRepository.getAllCompanies().first()
        return if (responseCompanies is RepositoryResponse.Success) {
            val companies = responseCompanies.data.mapIndexed { index, adaptiveCompany ->
                prepareStyles(adaptiveCompany, index, context)
                adaptiveCompany
            }
            val favouriteCompanies = mutableListOf<AdaptiveCompany>()
            companies.forEach { if (it.isFavourite) favouriteCompanies.add(it) }

            LocalInteractorResponse.Success(
                companies,
                favouriteCompanies
            )
        } else LocalInteractorResponse.Failed()
    }

    override suspend fun getSearchesData(context: Context): LocalInteractorResponse {
        val responseSearches = mRepository.getSearchesHistory().first()
        val popularRequests = mRepository.getPopularSearches()

        return if (responseSearches is RepositoryResponse.Success) {
            LocalInteractorResponse.Success(
                searchesHistory = responseSearches.data,
                popularRequests = popularRequests
            )
        } else LocalInteractorResponse.Failed()
    }

    override suspend fun updateCompany(adaptiveCompany: AdaptiveCompany) {
        mRepository.updateCompany(adaptiveCompany)
    }
}