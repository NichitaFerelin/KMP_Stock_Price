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

import com.ferelin.domain.repositories.searchRequests.SearchRequestsLoadState
import com.ferelin.domain.repositories.searchRequests.SearchRequestsRemoteRepo
import com.ferelin.domain.syncers.SearchRequestsSyncer
import com.ferelin.firebase.utils.itemsNotIn
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.withContext
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
        sourceRequests: List<String>
    ): List<String> {

        if (mIsDataSynchronized) {
            return emptyList()
        }

        val remoteRequestsState = withContext(mDispatchersProvider.IO) {
            mSearchRequestsRemoteRepo.loadSearchRequests(userToken)
        }

        return if (remoteRequestsState is SearchRequestsLoadState.Loaded) {
            val remoteRequests = remoteRequestsState.searchRequests
            syncCloudDb(userToken, sourceRequests, remoteRequests)

            remoteRequests.itemsNotIn(sourceRequests)
        } else {
            emptyList()
        }
    }

    override fun invalidate() {
        mIsDataSynchronized = false
    }

    private suspend fun syncCloudDb(
        userToken: String,
        sourceRequests: List<String>,
        remoteRequests: List<String>
    ): Unit = withContext(mDispatchersProvider.IO) {

        sourceRequests
            .itemsNotIn(remoteRequests)
            .forEach { mSearchRequestsRemoteRepo.cacheSearchRequest(userToken, it) }

        mIsDataSynchronized = true
    }
}