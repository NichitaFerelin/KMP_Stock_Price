package com.ferelin.local

import com.ferelin.local.database.CompaniesManagerHelper
import com.ferelin.local.json.JsonManagerHelper
import com.ferelin.local.prefs.StorePreferencesHelper
import com.ferelin.local.responses.CompaniesResponse
import com.ferelin.local.responses.SearchesResponse
import kotlinx.coroutines.flow.Flow

interface LocalManagerHelper : StorePreferencesHelper, CompaniesManagerHelper, JsonManagerHelper {

    fun getAllCompaniesAsResponse(): Flow<CompaniesResponse>

    fun getSearchesHistoryAsResponse(): Flow<SearchesResponse>
}