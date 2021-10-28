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

import com.ferelin.domain.repositories.searchRequests.SearchRequestsRemoteRepo
import com.ferelin.domain.syncers.SearchRequestsSyncer
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.ifPrepared
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class SearchRequestsSyncerImpl @Inject constructor(
    private val searchRequestsRemoteRepo: SearchRequestsRemoteRepo,
    private val dispatchersProvider: DispatchersProvider,
    @Named("ExternalScope") private val externalScope: CoroutineScope
) : SearchRequestsSyncer {

    private var isDataSynchronized: Boolean = false

    override suspend fun sync(
        userToken: String,
        sourceRequests: Set<String>
    ): Set<String> {
        Timber.d(
            "init data sync (is data synchronized = $isDataSynchronized, " +
                    "source requests size = ${sourceRequests.size}"
        )

        if (isDataSynchronized) {
            return emptySet()
        }

        // First load remote requests
        val remoteRequestsState = withContext(dispatchersProvider.IO) {
            searchRequestsRemoteRepo.loadAll(userToken).firstOrNull()
        }

        // If remote requests exists
        return remoteRequestsState?.ifPrepared { preparedState ->
            val remoteRequests = preparedState.data

            // Find difference between source and remote requests
            insertMissingItemsToRemote(userToken, sourceRequests, remoteRequests)

            // Return items that not exists at source requests
            remoteRequests
                .filterNot { sourceRequests.contains(it) }
                .toSet()

        } ?: emptySet()
    }

    override fun invalidate() {
        Timber.d("invalidate")
        isDataSynchronized = false
    }

    private suspend fun insertMissingItemsToRemote(
        userToken: String,
        sourceRequests: Set<String>,
        remoteRequests: Set<String>
    ) {
        sourceRequests
            .asSequence()
            .filterNot { remoteRequests.contains(it) }
            .onEach {
                externalScope.launch(dispatchersProvider.IO) {
                    searchRequestsRemoteRepo.insert(userToken, it)
                }
            }
            .toList()

        isDataSynchronized = true
    }
}