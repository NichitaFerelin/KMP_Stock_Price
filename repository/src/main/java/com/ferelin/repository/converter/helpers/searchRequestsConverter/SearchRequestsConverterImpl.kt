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

import com.ferelin.local.models.SearchRequest
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utils.Api
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.repository.utils.RepositoryResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRequestsConverterImpl @Inject constructor() : SearchRequestsConverter {

    override fun convertSearchRequestForLocal(
        searchRequest: AdaptiveSearchRequest
    ): SearchRequest {
        return SearchRequest(
            id = searchRequest.id,
            searchRequest = searchRequest.searchText
        )
    }

    override fun convertSearchRequestsForUi(
        searchRequests: List<SearchRequest>
    ): List<AdaptiveSearchRequest> {
        return searchRequests
            .map {
                AdaptiveSearchRequest(
                    id = it.id,
                    searchText = it.searchRequest
                )
            }.sortedBy { it.id }
    }

    override fun convertSearchRequestsTextForUi(
        response: BaseResponse<HashMap<Int, String>>?
    ): RepositoryResponse<List<AdaptiveSearchRequest>> {
        return if (response != null && response.responseCode == Api.RESPONSE_OK) {
            val data = mutableListOf<AdaptiveSearchRequest>()
            response.responseData!!.forEach { map ->
                data.add(
                    AdaptiveSearchRequest(
                        id = map.key,
                        searchText = map.value
                    )
                )
            }
            RepositoryResponse.Success(data = data)
        } else RepositoryResponse.Failed()
    }
}