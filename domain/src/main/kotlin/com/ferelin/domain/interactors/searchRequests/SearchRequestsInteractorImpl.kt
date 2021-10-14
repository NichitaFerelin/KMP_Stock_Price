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

package com.ferelin.domain.interactors.searchRequests

import com.ferelin.domain.repositories.searchRequests.SearchRequestsLocalRepo
import com.ferelin.domain.repositories.searchRequests.SearchRequestsRemoteRepo
import com.ferelin.domain.sources.AuthenticationSource
import com.ferelin.domain.syncers.SearchRequestsSyncer
import com.ferelin.shared.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

typealias SearchRequests = List<String>

@Singleton
class SearchRequestsInteractorImpl @Inject constructor(
    private val mSearchRequestsLocalRepo: SearchRequestsLocalRepo,
    private val mSearchRequestsRemoteRepo: SearchRequestsRemoteRepo,
    private val mSearchRequestsSyncer: SearchRequestsSyncer,
    private val mAuthenticationSource: AuthenticationSource,
    private val mDispatchersProvider: DispatchersProvider,
    @Named("ExternalScope") private val mExternalScope: CoroutineScope
) : SearchRequestsInteractor, AuthenticationListener, NetworkListener {

    private val mSearchRequestsState = MutableStateFlow<LoadState<SearchRequests>>(LoadState.None())
    override val searchRequestsState: StateFlow<LoadState<SearchRequests>>
        get() = mSearchRequestsState.asStateFlow()

    private var mPopularRequestsState: LoadState<SearchRequests> = LoadState.None()

    private companion object {
        const val sCachedRequestsLimit = 30
    }

    override suspend fun getSearchRequests(): List<String> {
        mSearchRequestsState.value.ifPrepared { preparedState ->
            return preparedState.data
        }

        mSearchRequestsState.value = LoadState.Loading()

        return mSearchRequestsLocalRepo.getSearchRequests()
            .also {
                mSearchRequestsState.value = LoadState.Prepared(it)
                tryToSync()
            }
    }

    override suspend fun getPopularSearchRequests(): List<String> {
        mSearchRequestsState.value.ifPrepared { preparedState ->
            return preparedState.data
        }

        mPopularRequestsState = LoadState.Loading()

        return mSearchRequestsLocalRepo.getPopularSearchRequests()
            .also { mPopularRequestsState = LoadState.Prepared(it) }
    }

    override suspend fun cacheSearchRequest(searchRequest: String): List<String> =
        withContext(mDispatchersProvider.IO) {

            var updatedSearchRequests = mutableListOf<String>()

            mExternalScope.launch(mDispatchersProvider.IO) {
                mSearchRequestsState.value.ifPrepared { preparedState ->
                    updatedSearchRequests = removeDuplicates(
                        sourceRequests = preparedState.data,
                        newSearchRequest = searchRequest
                    )
                    updatedSearchRequests.add(0, searchRequest)

                    if (updatedSearchRequests.size > sCachedRequestsLimit) {
                        reduceRequestsToLimit(updatedSearchRequests)
                    }

                    mSearchRequestsState.value = LoadState.Prepared(updatedSearchRequests)
                }

                cache(searchRequest)
            }.join()

            updatedSearchRequests.toList()
        }

    private suspend fun reduceRequestsToLimit(requests: MutableList<String>) =
        withContext(mDispatchersProvider.IO) {

            while (requests.size > sCachedRequestsLimit) {
                val removedRequest = requests.removeLast()

                launch { erase(removedRequest) }
            }
        }

    private suspend fun removeDuplicates(
        sourceRequests: List<String>,
        newSearchRequest: String
    ): MutableList<String> = withContext(mDispatchersProvider.IO) {

        val noDuplicatesRequests = sourceRequests.toMutableList()
        val newRequestLower = newSearchRequest.lowercase()

        noDuplicatesRequests
            .toList()
            .asSequence()
            .filter { previousRequest ->
                newRequestLower.contains(previousRequest.lowercase())
            }
            .onEachIndexed { index, requestToRemove ->
                launch { erase(requestToRemove) }

                noDuplicatesRequests.removeAt(index)
            }
            .toList()

        noDuplicatesRequests
    }

    override suspend fun onLogIn() {
        tryToSync()
    }

    override suspend fun onLogOut() {
        mSearchRequestsState.value = LoadState.Prepared(emptyList())

        mExternalScope.launch(mDispatchersProvider.IO) {
            mSearchRequestsLocalRepo.clearSearchRequests()
        }
    }

    override suspend fun onNetworkAvailable() {
        tryToSync()
    }

    override suspend fun onNetworkLost() {
        mSearchRequestsSyncer.invalidate()
    }

    private suspend fun cache(searchRequest: String) {
        mSearchRequestsLocalRepo.cacheSearchRequest(searchRequest)

        mAuthenticationSource.getUserToken()?.let { userToken ->
            mSearchRequestsRemoteRepo.cacheSearchRequest(userToken, searchRequest)
        }
    }

    private suspend fun erase(searchRequest: String) {
        mSearchRequestsLocalRepo.eraseSearchRequest(searchRequest)

        mAuthenticationSource.getUserToken()?.let { userToken ->
            mSearchRequestsRemoteRepo.eraseSearchRequest(userToken, searchRequest)
        }
    }

    private suspend fun tryToSync() {
        mSearchRequestsState.value.ifPrepared { preparedState ->
            mAuthenticationSource.getUserToken()?.let { userToken ->
                mSearchRequestsSyncer
                    .initDataSync(userToken, preparedState.data)
                    .forEach { remoteSearchRequest ->
                        cacheSearchRequest(remoteSearchRequest)
                    }
            }
        }
    }
}