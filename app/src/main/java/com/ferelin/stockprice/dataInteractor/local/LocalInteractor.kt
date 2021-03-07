package com.ferelin.stockprice.dataInteractor.local

import android.content.Context
import com.ferelin.repository.RepositoryManagerHelper
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.utilits.RepositoryResponse
import kotlinx.coroutines.flow.first

class LocalInteractor(private val mRepository: RepositoryManagerHelper) : LocalInteractorHelper {

    override suspend fun prepareData(context: Context): LocalInteractorResponse {
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

    override suspend fun updateCompany(adaptiveCompany: AdaptiveCompany) {
        mRepository.updateCompany(adaptiveCompany)
    }
}