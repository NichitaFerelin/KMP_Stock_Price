package com.ferelin.local

import com.ferelin.local.databases.companies.CompaniesManagerHelper
import com.ferelin.local.databases.searchesHistory.SearchesHistoryManagerHelper
import com.ferelin.local.json.JsonManagerHelper
import com.ferelin.local.model.CompaniesResponse
import kotlinx.coroutines.flow.Flow

interface LocalManagerHelper
    : SearchesHistoryManagerHelper, CompaniesManagerHelper, JsonManagerHelper {

    fun getAllCompaniesAsResponse(): Flow<CompaniesResponse>
}