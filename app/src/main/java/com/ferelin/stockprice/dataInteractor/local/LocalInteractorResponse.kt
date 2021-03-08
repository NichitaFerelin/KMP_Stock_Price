package com.ferelin.stockprice.dataInteractor.local

import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveSearch

sealed class LocalInteractorResponse {
    data class Success(
        val companies: List<AdaptiveCompany> = emptyList(),
        val favouriteCompanies: List<AdaptiveCompany> = emptyList(),
        val searchesHistory: List<AdaptiveSearch> = emptyList(),
        val popularRequests: List<AdaptiveSearch> = emptyList()
    ) : LocalInteractorResponse()

    data class Failed(val error: String? = null) : LocalInteractorResponse()
}
