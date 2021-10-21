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

package com.ferelin.data_network_firebase.syncers

import com.ferelin.domain.entities.SearchRequest
import com.ferelin.domain.repositories.searchRequests.SearchRequestsRemoteRepo
import com.ferelin.domain.syncers.SearchRequestsSyncer
import com.ferelin.data_network_firebase.utils.itemsNotIn
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.ifPrepared
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRequestsSyncerImpl @Inject constructor(
    private val searchRequestsRemoteRepo: SearchRequestsRemoteRepo,
    private val dispatchersProvider: DispatchersProvider
) : SearchRequestsSyncer {

    private var isDataSynchronized: Boolean = false

    override suspend fun initDataSync(
        userToken: String,
        sourceRequests: List<SearchRequest>
    ): List<SearchRequest> {
        Timber.d(
            "init data sync (is data synchronized = $isDataSynchronized, " +
                    "source requests size = ${sourceRequests.size}"
        )

        if (isDataSynchronized) {
            return emptyList()
        }

        val remoteRequestsState = withContext(dispatchersProvider.IO) {
            searchRequestsRemoteRepo.loadAll(userToken).firstOrNull()
        }

        return remoteRequestsState?.ifPrepared { preparedState ->
            val remoteRequests = preparedState.data
            syncCloudDb(userToken, sourceRequests, remoteRequests)

            remoteRequests.itemsNotIn(sourceRequests)
        } ?: emptyList()
    }

    override fun invalidate() {
        Timber.d("invalidate")
        isDataSynchronized = false
    }

    private suspend fun syncCloudDb(
        userToken: String,
        sourceRequests: List<SearchRequest>,
        remoteRequests: List<SearchRequest>
    ): Unit = withContext(dispatchersProvider.IO) {

        // Source requests list is reversed.
        // To avoid rewriting the query on the cloud database, instead of adding it,
        // needs to change its id to a unique one
        val lastRemoteId = remoteRequests.lastOrNull()?.id ?: 0
        val lastSourceId = sourceRequests.firstOrNull()?.id ?: 0

        var lastId = if (lastRemoteId > lastSourceId) {
            lastRemoteId
        } else {
            lastSourceId
        }

        sourceRequests
            .itemsNotIn(remoteRequests)
            .forEach {
                it.id = ++lastId
                searchRequestsRemoteRepo.insert(userToken, it)
            }

        isDataSynchronized = true
    }
}