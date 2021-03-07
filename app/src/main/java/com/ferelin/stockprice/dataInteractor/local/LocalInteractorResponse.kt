package com.ferelin.stockprice.dataInteractor.local

import com.ferelin.repository.adaptiveModels.AdaptiveCompany

sealed class LocalInteractorResponse {
    data class Success(
        val companies: List<AdaptiveCompany> = emptyList(),
        val favouriteCompanies: List<AdaptiveCompany> = emptyList()
    ) : LocalInteractorResponse()

    data class Failed(val error: String? = null) : LocalInteractorResponse()
}
