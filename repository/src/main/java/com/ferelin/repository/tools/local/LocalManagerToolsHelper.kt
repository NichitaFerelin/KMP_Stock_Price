package com.ferelin.repository.tools.local

import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import kotlinx.coroutines.flow.Flow

interface LocalManagerToolsHelper {

    fun getAllCompanies(): Flow<List<AdaptiveCompany>>

    fun insertCompany(company: AdaptiveCompany)
}