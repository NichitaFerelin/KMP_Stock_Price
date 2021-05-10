package com.ferelin.stockprice.dataInteractor.local

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

import com.ferelin.repository.RepositoryManagerHelper
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.repository.utils.RepositoryResponse
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [LocalInteractor] is responsible for local requests.
 */

@Singleton
class LocalInteractor @Inject constructor(
    private val mRepository: RepositoryManagerHelper
) : LocalInteractorHelper {

    override suspend fun getCompanies(): LocalInteractorResponse {
        val responseCompanies = mRepository.getAllCompanies().firstOrNull()
        return if (responseCompanies is RepositoryResponse.Success) {
            LocalInteractorResponse.Success(responseCompanies.data)
        } else LocalInteractorResponse.Failed()
    }

    override suspend fun getSearchRequestsHistory(): LocalInteractorResponse {
        val responseSearches = mRepository.getSearchesHistory().firstOrNull()
        return if (responseSearches is RepositoryResponse.Success) {
            LocalInteractorResponse.Success(searchesHistory = responseSearches.data)
        } else LocalInteractorResponse.Failed()
    }

    override suspend fun cacheCompany(adaptiveCompany: AdaptiveCompany) {
        mRepository.saveCompanyData(adaptiveCompany)
    }

    override suspend fun cacheSearchRequestsHistory(requests: List<AdaptiveSearchRequest>) {
        mRepository.setSearchesHistory(requests)
    }

    override suspend fun setFirstTimeLaunchState(state: Boolean) {
        mRepository.setFirstTimeLaunchState(state)
    }

    override suspend fun getFirstTimeLaunchState(): LocalInteractorResponse {
        val firstTimeStateResponse = mRepository.getFirstTimeLaunchState().firstOrNull()
        return if (firstTimeStateResponse is RepositoryResponse.Success) {
            LocalInteractorResponse.Success(firstTimeLaunch = firstTimeStateResponse.data)
        } else LocalInteractorResponse.Failed()
    }
}