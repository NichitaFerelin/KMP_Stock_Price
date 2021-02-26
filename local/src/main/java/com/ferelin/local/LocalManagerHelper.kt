package com.ferelin.local

import com.ferelin.local.database.CompaniesManagerHelper
import com.ferelin.local.json.JsonManagerHelper
import com.ferelin.local.model.CompaniesResponse
import kotlinx.coroutines.flow.Flow

interface LocalManagerHelper : CompaniesManagerHelper, JsonManagerHelper {
    fun getAllCompaniesAsResponse() : Flow<CompaniesResponse>
}