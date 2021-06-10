package com.ferelin.local

/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.ferelin.local.database.CompaniesManager
import com.ferelin.local.json.JsonManager
import com.ferelin.local.preferences.StorePreferences
import com.ferelin.local.responses.CompaniesResponse
import com.ferelin.local.responses.SearchesResponse

interface LocalManager : StorePreferences, CompaniesManager, JsonManager {

    fun getAllCompanies(): CompaniesResponse

    suspend fun getSearchesHistoryAsResponse(): SearchesResponse
}