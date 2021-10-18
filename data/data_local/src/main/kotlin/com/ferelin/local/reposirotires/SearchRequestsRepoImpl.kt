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
import com.ferelin.local.sources.PopularRequestsSource
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class SearchRequestsRepoImpl @Inject constructor(
    private val searchRequestsDao: SearchRequestsDao,
    private val searchRequestsMapper: SearchRequestMapper,
    private val dispatchersProvider: DispatchersProvider
) : SearchRequestsLocalRepo {

    override suspend fun insert(searchRequest: SearchRequest): Unit =
        withContext(dispatchersProvider.IO) {
            Timber.d("insert (search request = $searchRequest)")

            val searchRequestDBO = searchRequestsMapper.map(searchRequest)
            searchRequestsDao.insert(searchRequestDBO)
        }

    override suspend fun getAll(): List<SearchRequest> =
        withContext(dispatchersProvider.IO) {
            Timber.d("get all")

            return@withContext searchRequestsDao
                .getAll()
                .map(searchRequestsMapper::map)
        }

    override suspend fun getAllPopular(): List<SearchRequest> {
        Timber.d("get all popular")

        return PopularRequestsSource.popularSearchRequests
    }

    override suspend fun eraseAll(): Unit =
        withContext(dispatchersProvider.IO) {
            Timber.d("erase all")

            searchRequestsDao.eraseAll()
        }

    override suspend fun erase(searchRequest: SearchRequest): Unit =
        withContext(dispatchersProvider.IO) {
            Timber.d("erase (searchRequest = $searchRequest)")

            val searchRequestDBO = searchRequestsMapper.map(searchRequest)
            searchRequestsDao.erase(searchRequestDBO)
        }
}