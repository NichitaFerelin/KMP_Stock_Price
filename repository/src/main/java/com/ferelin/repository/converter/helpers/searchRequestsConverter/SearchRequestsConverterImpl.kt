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

package com.ferelin.repository.converter.helpers.searchRequestsConverter

import com.ferelin.local.responses.SearchesResponse
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.repository.utils.RepositoryResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRequestsConverterImpl @Inject constructor() : SearchRequestsConverter {

    override fun convertSearchesForLocal(search: List<AdaptiveSearchRequest>): Set<String> {
        val dataSet = mutableSetOf<String>()
        search.forEach { dataSet.add(it.searchText) }
        return dataSet
    }

    override fun convertSearchesForUi(
        response: SearchesResponse
    ): RepositoryResponse<List<AdaptiveSearchRequest>> {
        return if (response is SearchesResponse.Success) {
            val convertedData = response.data.map { AdaptiveSearchRequest(it) }
            RepositoryResponse.Success(data = convertedData)
        } else RepositoryResponse.Failed()
    }
}