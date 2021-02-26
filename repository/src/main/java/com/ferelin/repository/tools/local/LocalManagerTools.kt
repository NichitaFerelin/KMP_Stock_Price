package com.ferelin.repository.tools.local

import com.ferelin.local.LocalManagerHelper
import com.ferelin.local.model.Responses
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.tools.CommonTransformer
import com.ferelin.repository.tools.CommonTransformer.toDatabaseCompany
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalManagerTools(private val mLocalManager: LocalManagerHelper) : LocalManagerToolsHelper {

    override fun getAllCompanies(): Flow<List<AdaptiveCompany>> {
        return mLocalManager.getAllCompaniesAsResponse().map { response ->
            if (response.code == Responses.LOADED_FROM_JSON) {
                response.data.map { CommonTransformer.toAdaptiveCompanyWithTransform(it) }
            } else response.data.map { CommonTransformer.toAdaptiveCompany(it) }
        }
    }

    override fun insertCompany(company: AdaptiveCompany) {
        mLocalManager.insertCompany(toDatabaseCompany(company))
    }
}