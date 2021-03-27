package com.ferelin.stockprice.dataInteractor.local

import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest

sealed class LocalInteractorResponse {
    data class Success(
        val companies: List<AdaptiveCompany> = emptyList(),
        val searchesHistory: List<AdaptiveSearchRequest> = emptyList(),
        val firstTimeLaunch: Boolean = false
    ) : LocalInteractorResponse()

    data class Failed(val error: String? = null) : LocalInteractorResponse()
}
