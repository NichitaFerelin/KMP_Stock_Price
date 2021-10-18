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

package com.ferelin.firebase.syncers

import android.util.Log
import com.ferelin.domain.entities.SearchRequest
import com.ferelin.domain.repositories.searchRequests.SearchRequestsRemoteRepo
import com.ferelin.domain.syncers.SearchRequestsSyncer
import com.ferelin.firebase.utils.itemsNotIn
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.ifPrepared
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRequestsSyncerImpl @Inject constructor(
    private val mSearchRequestsRemoteRepo: SearchRequestsRemoteRepo,
    private val mDispatchersProvider: DispatchersProvider
) : SearchRequestsSyncer {

    private var mIsDataSynchronized: Boolean = false

    override suspend fun initDataSync(
        userToken: String,
        sourceRequests: List<SearchRequest>
    ): List<SearchRequest> {
        Timber.d(
            "init data sync (isDataSynchronized = $mIsDataSynchronized, " +
                    "userToken = $userToken, sourceRequestsSize = ${sourceRequests.size}"
        )

        if (mIsDataSynchronized) {
            return emptyList()
        }

        val remoteRequestsState = withContext(mDispatchersProvider.IO) {
            mSearchRequestsRemoteRepo.loadSearchRequests(userToken).firstOrNull()
        }

        return remoteRequestsState?.ifPrepared { preparedState ->
            val remoteRequests = preparedState.data
            syncCloudDb(userToken, sourceRequests, remoteRequests)

            remoteRequests.itemsNotIn(sourceRequests)
        } ?: emptyList()
    }

    override fun invalidate() {
        Timber.d("invalidate")
        mIsDataSynchronized = false
    }

    private suspend fun syncCloudDb(
        userToken: String,
        sourceRequests: List<SearchRequest>,
        remoteRequests: List<SearchRequest>
    ): Unit = withContext(mDispatchersProvider.IO) {
        Timber.d(
            "sync cloud db (sources = ${sourceRequests.size}, " +
                    "remotes = ${remoteRequests.size})"
        )

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
                mSearchRequestsRemoteRepo.cacheSearchRequest(userToken, it)
            }

        mIsDataSynchronized = true
    }
}