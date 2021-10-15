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

package com.ferelin.local.reposirotires

import com.ferelin.domain.entities.SearchRequest
import com.ferelin.domain.repositories.searchRequests.SearchRequestsLocalRepo
import com.ferelin.local.database.SearchRequestsDao
import com.ferelin.local.mappers.SearchRequestMapper
import com.ferelin.local.utils.PopularRequestsSource
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class SearchRequestsRepoImpl @Inject constructor(
    private val mSearchRequestsDao: SearchRequestsDao,
    private val mSearchRequestMapper: SearchRequestMapper,
    private val mDispatchersProvider: DispatchersProvider
) : SearchRequestsLocalRepo {

    override suspend fun cacheSearchRequest(searchRequest: SearchRequest): Unit =
        withContext(mDispatchersProvider.IO) {
            Timber.d("cache search request (search request = $searchRequest)")
            val mappedRequest = mSearchRequestMapper.map(searchRequest)
            mSearchRequestsDao.insert(mappedRequest)
        }

    override suspend fun eraseSearchRequest(searchRequest: SearchRequest): Unit =
        withContext(mDispatchersProvider.IO) {
            Timber.d("erase search request (searchRequest = $searchRequest)")
            val mappedRequest = mSearchRequestMapper.map(searchRequest)
            mSearchRequestsDao.remove(mappedRequest)
        }

    override suspend fun getSearchRequests(): List<SearchRequest> =
        withContext(mDispatchersProvider.IO) {
            Timber.d("get search requests")
            return@withContext mSearchRequestsDao
                .getAll()
                .map(mSearchRequestMapper::map)
        }

    override suspend fun getPopularSearchRequests(): List<SearchRequest> {
        Timber.d("get popular search requests")
        return PopularRequestsSource.popularSearchRequests
    }

    override suspend fun clearSearchRequests(): Unit =
        withContext(mDispatchersProvider.IO) {
            Timber.d("clear search requests")
            mSearchRequestsDao.clearSearchRequests()
        }
}