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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class SearchRequestsInteractorImpl @Inject constructor(
    private val searchRequestsLocalRepo: SearchRequestsLocalRepo,
    private val searchRequestsRemoteRepo: SearchRequestsRemoteRepo,
    private val searchRequestsSyncer: SearchRequestsSyncer,
    private val authenticationSource: AuthenticationSource,
    @Named(NAMED_EXTERNAL_SCOPE) private val externalScope: CoroutineScope
) : SearchRequestsInteractor, AuthenticationListener, NetworkListener {

    private val _searchRequestsState = MutableStateFlow<LoadState<Set<String>>>(LoadState.None())
    override val searchRequestsState: StateFlow<LoadState<Set<String>>> =
        _searchRequestsState.asStateFlow()

    private var popularRequestsState: LoadState<Set<String>> = LoadState.None()

    private var cacheJob: Job? = null

    override suspend fun getAll(): Set<String> {
        _searchRequestsState.value.ifPrepared { preparedState ->
            return preparedState.data
        }

        _searchRequestsState.value = LoadState.Loading()

        val dbRequests = searchRequestsLocalRepo.getAll()
        _searchRequestsState.value = LoadState.Prepared(dbRequests)

        externalScope.launch {
            tryToSync()
        }

        return dbRequests
    }

    override suspend fun getAllPopular(): Set<String> {
        popularRequestsState.ifPrepared { preparedState ->
            return preparedState.data
        }

        popularRequestsState = LoadState.Loading()

        return searchRequestsLocalRepo.getAllPopular()
            .also { popularRequestsState = LoadState.Prepared(it) }
    }

    override suspend fun cache(searchRequest: String) {
        cacheJob?.join()
        cacheJob = externalScope.launch {
            _searchRequestsState.value.ifPrepared { preparedState ->

                val updatedSearchRequests = removeDuplicates(
                    sourceRequests = preparedState.data,
                    newSearchRequest = searchRequest
                )
                updatedSearchRequests.add(searchRequest)

                searchRequestsLocalRepo.insert(updatedSearchRequests)

                authenticationSource.getUserToken()?.let { userToken ->
                    searchRequestsRemoteRepo.insert(userToken, searchRequest)
                }

                _searchRequestsState.value = LoadState.Prepared(updatedSearchRequests)
            }
        }
    }

    private suspend fun removeDuplicates(
        sourceRequests: Set<String>,
        newSearchRequest: String
    ): MutableSet<String>  {

        val noDuplicatesRequests = sourceRequests.toMutableSet()
        val newRequestLower = newSearchRequest.lowercase()

        noDuplicatesRequests
            // avoid concurrency exception
            .toSet()
            .asSequence()
            .filter { newRequestLower.contains(it.lowercase()) }
            .onEach { requestToRemove ->
                noDuplicatesRequests.remove(requestToRemove)

                externalScope.launch {
                    erase(noDuplicatesRequests, requestToRemove)
                }
            }
            .toSet()

        return noDuplicatesRequests
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
        externalScope.launch {
            searchRequestsLocalRepo.eraseAll()

            authenticationSource.getUserToken()?.let { userToken ->
                searchRequestsRemoteRepo.eraseAll(userToken)
            }

            _searchRequestsState.value = LoadState.Prepared(emptySet())
        }
    }

    private suspend fun erase(resultItems: Set<String>, searchRequest: String) {
        searchRequestsLocalRepo.insert(resultItems)

        authenticationSource.getUserToken()?.let { userToken ->
            searchRequestsRemoteRepo.erase(userToken, searchRequest)
        }
    }

    private suspend fun tryToSync() {
        _searchRequestsState.value.ifPrepared { preparedState ->
            authenticationSource.getUserToken()?.let { userToken ->

                val remoteSearchRequests = searchRequestsSyncer.sync(userToken, preparedState.data)
                remoteSearchRequests.forEach { remoteSearchRequest ->
                    cache(remoteSearchRequest)
                }
            }
        }
    }
}