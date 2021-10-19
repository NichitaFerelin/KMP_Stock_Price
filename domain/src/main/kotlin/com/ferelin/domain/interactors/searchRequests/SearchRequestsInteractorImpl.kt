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

import com.ferelin.domain.entities.SearchRequest
import com.ferelin.domain.repositories.searchRequests.SearchRequestsLocalRepo
import com.ferelin.domain.repositories.searchRequests.SearchRequestsRemoteRepo
import com.ferelin.domain.sources.AuthenticationSource
import com.ferelin.domain.syncers.SearchRequestsSyncer
import com.ferelin.shared.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class SearchRequestsInteractorImpl @Inject constructor(
    private val searchRequestsLocalRepo: SearchRequestsLocalRepo,
    private val searchRequestsRemoteRepo: SearchRequestsRemoteRepo,
    private val searchRequestsSyncer: SearchRequestsSyncer,
    private val authenticationSource: AuthenticationSource,
    private val dispatchersProvider: DispatchersProvider,
    @Named("ExternalScope") private val externalScope: CoroutineScope
) : SearchRequestsInteractor, AuthenticationListener, NetworkListener {

    private val _searchRequestsState =
        MutableStateFlow<LoadState<List<SearchRequest>>>(LoadState.None())
    override val searchRequestsState: StateFlow<LoadState<List<SearchRequest>>> =
        _searchRequestsState.asStateFlow()

    private var popularRequestsState: LoadState<List<SearchRequest>> = LoadState.None()

    private var cacheJob: Job? = null

    private companion object {
        const val CACHED_REQUESTS_LIMIT = 30
    }

    override suspend fun getAll(): List<SearchRequest> {
        _searchRequestsState.value.ifPrepared { preparedState ->
            return preparedState.data
        }

        _searchRequestsState.value = LoadState.Loading()

        // Recently created requests should be first in the list
        val dbRequests = searchRequestsLocalRepo
            .getAll()
            .sortedByDescending { it.id }

        _searchRequestsState.value = LoadState.Prepared(dbRequests)

        externalScope.launch(dispatchersProvider.IO) {
            tryToSync()
        }

        return dbRequests
    }

    override suspend fun getAllPopular(): List<SearchRequest> {
        popularRequestsState.ifPrepared { preparedState ->
            return preparedState.data
        }

        popularRequestsState = LoadState.Loading()

        return searchRequestsLocalRepo.getAllPopular()
            .also { popularRequestsState = LoadState.Prepared(it) }
    }

    override suspend fun cache(searchText: String): Unit =
        withContext(dispatchersProvider.IO) {

            // New Search Request item ID creates sequentially
            // So that there are no duplicates it is necessary to wait for job
            cacheJob?.join()
            cacheJob = externalScope.launch(dispatchersProvider.IO) {
                _searchRequestsState.value.ifPrepared { preparedState ->

                    // Last created item is first at list
                    // Next id is creates by previous one
                    val searchRequest = SearchRequest(
                        id = preparedState.data.firstOrNull()?.id?.plus(1) ?: 0,
                        request = searchText
                    )

                    val updatedSearchRequests = removeDuplicates(
                        sourceRequests = preparedState.data,
                        newSearchRequest = searchRequest
                    )
                    updatedSearchRequests.add(0, searchRequest)

                    if (updatedSearchRequests.size > CACHED_REQUESTS_LIMIT) {
                        reduceRequestsToLimit(updatedSearchRequests)
                    }

                    _searchRequestsState.value = LoadState.Prepared(updatedSearchRequests)

                    searchRequestsLocalRepo.insert(searchRequest)

                    // TODO Rewrites requests at cloud DB
                    // TODO ID's inconsistency
                    authenticationSource.getUserToken()?.let { userToken ->
                        searchRequestsRemoteRepo.insert(userToken, searchRequest)
                    }
                }
            }
        }

    private suspend fun reduceRequestsToLimit(requests: MutableList<SearchRequest>) =
        withContext(dispatchersProvider.IO) {
            val removedRequest = requests.removeLast()
            launch { erase(removedRequest) }
        }

    private suspend fun removeDuplicates(
        sourceRequests: List<SearchRequest>,
        newSearchRequest: SearchRequest
    ): MutableList<SearchRequest> = withContext(dispatchersProvider.IO) {

        val noDuplicatesRequests = sourceRequests.toMutableList()
        val newRequestLower = newSearchRequest.request.lowercase()

        noDuplicatesRequests
            .toList()
            .asSequence()
            .filter { previousRequest ->
                newRequestLower.contains(previousRequest.request.lowercase())
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
        invalidateUserData()
        searchRequestsSyncer.invalidate()
    }

    override suspend fun onNetworkAvailable() {
        tryToSync()
    }

    override suspend fun onNetworkLost() {
        searchRequestsSyncer.invalidate()
    }

    override suspend fun eraseUserData() {
        invalidateUserData()
    }

    private fun invalidateUserData() {
        externalScope.launch(dispatchersProvider.IO) {
            searchRequestsLocalRepo.eraseAll()

            authenticationSource.getUserToken()?.let { userToken ->
                searchRequestsRemoteRepo.eraseAll(userToken)
            }

            _searchRequestsState.value = LoadState.Prepared(emptyList())
        }
    }

    private suspend fun erase(searchRequest: SearchRequest) {
        searchRequestsLocalRepo.erase(searchRequest)

        authenticationSource.getUserToken()?.let { userToken ->
            searchRequestsRemoteRepo.erase(userToken, searchRequest)
        }
    }

    private suspend fun tryToSync() {
        _searchRequestsState.value.ifPrepared { preparedState ->
            authenticationSource.getUserToken()?.let { userToken ->

                searchRequestsSyncer
                    .initDataSync(userToken, preparedState.data)
                    .forEach { remoteSearchRequest ->
                        cache(remoteSearchRequest.request)
                    }
            }
        }
    }
}