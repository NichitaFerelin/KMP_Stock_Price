package com.ferelin.local

import com.ferelin.local.database.CompaniesManagerHelper
import com.ferelin.local.json.JsonManagerHelper
import com.ferelin.local.model.CompaniesResponse
import com.ferelin.local.model.PreferencesResponse
import com.ferelin.local.prefs.StorePreferencesHelper
import kotlinx.coroutines.flow.Flow

interface LocalManagerHelper : StorePreferencesHelper, CompaniesManagerHelper, JsonManagerHelper {

    fun getAllCompaniesAsResponse(): Flow<CompaniesResponse>

    fun getSearchesHistoryAsResponse(): Flow<PreferencesResponse>
}