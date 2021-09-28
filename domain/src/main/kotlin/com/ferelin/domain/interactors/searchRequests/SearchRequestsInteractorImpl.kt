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
import com.ferelin.shared.AuthenticationListener
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.NetworkListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

sealed class SearchRequestsState {
    class Prepared(val searchRequests: List<String>) : SearchRequestsState()
    object Loading : SearchRequestsState()
    object None : SearchRequestsState()
}

@Singleton
class SearchRequestsInteractorImpl @Inject constructor(
    private val mSearchRequestsLocalRepo: SearchRequestsLocalRepo,
    private val mSearchRequestsRemoteRepo: SearchRequestsRemoteRepo,
    private val mSearchRequestsSyncer: SearchRequestsSyncer,
    private val mAuthenticationSource: AuthenticationSource,
    private val mDispatchersProvider: DispatchersProvider,
    @Named("ExternalScope") private val mExternalScope: CoroutineScope
) : SearchRequestsInteractor, AuthenticationListener, NetworkListener {

    private val mSearchRequestsState =
        MutableStateFlow<SearchRequestsState>(SearchRequestsState.None)

    private var mPopularRequestsState: SearchRequestsState = SearchRequestsState.None

    private companion object {
        const val sCachedRequestsLimit = 30
    }

    override suspend fun getSearchRequests(): List<String> {
        if (mSearchRequestsState.value is SearchRequestsState.Prepared) {
            return (mSearchRequestsState.value as SearchRequestsState.Prepared).searchRequests
        }

        mSearchRequestsState.value = SearchRequestsState.Loading

        return mSearchRequestsLocalRepo.getSearchRequests()
            .also {
                mSearchRequestsState.value = SearchRequestsState.Prepared(it)
                tryToSync()
            }
    }

    override suspend fun getPopularSearchRequests(): List<String> {
        if (mPopularRequestsState is SearchRequestsState.Prepared) {
            return (mPopularRequestsState as SearchRequestsState.Prepared).searchRequests
        }

        mPopularRequestsState = SearchRequestsState.Loading

        return mSearchRequestsLocalRepo.getPopularSearchRequests()
            .also { mPopularRequestsState = SearchRequestsState.Prepared(it) }
    }

    override suspend fun cacheSearchRequest(searchRequest: String) {
        mExternalScope.launch(mDispatchersProvider.IO) {

            mSearchRequestsState.value.let { requestsState ->
                if (requestsState is SearchRequestsState.Prepared) {
                    val requestsWithoutDuplicates = removeDuplicates(
                        sourceRequests = requestsState.searchRequests,
                        newSearchRequest = searchRequest
                    )
                    requestsWithoutDuplicates.add(0, searchRequest)

                    if (requestsWithoutDuplicates.size > sCachedRequestsLimit) {
                        reduceRequestsToLimit(requestsWithoutDuplicates)
                    }

                    mSearchRequestsState.value =
                        SearchRequestsState.Prepared(requestsWithoutDuplicates)
                }
            }

            cache(searchRequest)
        }
    }

    override fun observeSearchRequestsUpdates(): StateFlow<SearchRequestsState> {
        return mSearchRequestsState.asStateFlow()
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

        sourceRequests
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
        mSearchRequestsState.value = SearchRequestsState.Prepared(emptyList())

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
        mSearchRequestsState.value.let { searchRequestsState ->
            if (searchRequestsState !is SearchRequestsState.Prepared) {
                return
            }

            mAuthenticationSource.getUserToken()?.let { userToken ->
                mSearchRequestsSyncer
                    .initDataSync(userToken, searchRequestsState.searchRequests)
                    .forEach { remoteSearchRequest ->
                        cacheSearchRequest(remoteSearchRequest)
                    }
            }
        }
    }
}