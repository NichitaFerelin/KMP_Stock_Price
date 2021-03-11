package com.ferelin.local.responses

import com.ferelin.local.models.Company

sealed class CompaniesResponse {
    class Success(
        val code: Int = Responses.LOADED_FROM_DB,
        val companies: List<Company>
    ) : CompaniesResponse()

    object Failed : CompaniesResponse()
}

