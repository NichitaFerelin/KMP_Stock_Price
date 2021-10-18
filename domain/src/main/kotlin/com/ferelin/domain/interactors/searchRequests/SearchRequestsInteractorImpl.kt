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

import android.util.Log
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

typealias SearchRequestsState = LoadState<List<SearchRequest>>

@Singleton
class SearchRequestsInteractorImpl @Inject constructor(
    private val mSearchRequestsLocalRepo: SearchRequestsLocalRepo,
    private val mSearchRequestsRemoteRepo: SearchRequestsRemoteRepo,
    private val mSearchRequestsSyncer: SearchRequestsSyncer,
    private val mAuthenticationSource: AuthenticationSource,
    private val mDispatchersProvider: DispatchersProvider,
    @Named("ExternalScope") private val mExternalScope: CoroutineScope
) : SearchRequestsInteractor, AuthenticationListener, NetworkListener {

    private val mSearchRequestsState = MutableStateFlow<SearchRequestsState>(LoadState.None())
    override val searchRequestsState: StateFlow<SearchRequestsState>
        get() = mSearchRequestsState.asStateFlow()

    private var mPopularRequestsState: SearchRequestsState = LoadState.None()

    private companion object {
        const val sCachedRequestsLimit = 30
    }

    override suspend fun getSearchRequests(): List<SearchRequest> {
        mSearchRequestsState.value.ifPrepared { preparedState ->
            return preparedState.data
        }

        mSearchRequestsState.value = LoadState.Loading()

        val dbRequests = mSearchRequestsLocalRepo
            .getSearchRequests()
            .sortedByDescending { it.id }

        mSearchRequestsState.value = LoadState.Prepared(dbRequests)
        mExternalScope.launch(mDispatchersProvider.IO) {
            tryToSync()
        }

        return dbRequests
    }

    override suspend fun getPopularSearchRequests(): List<SearchRequest> {
        mPopularRequestsState.ifPrepared { preparedState ->
            return preparedState.data
        }

        mPopularRequestsState = LoadState.Loading()

        return mSearchRequestsLocalRepo.getPopularSearchRequests()
            .also { mPopularRequestsState = LoadState.Prepared(it) }
    }

    private var mCacheJob: Job? = null

    override suspend fun cacheSearchRequest(searchText: String, toNetwork: Boolean): Unit =
        withContext(mDispatchersProvider.IO) {

            mCacheJob?.join()
            mCacheJob = mExternalScope.launch(mDispatchersProvider.IO) {
                mSearchRequestsState.value.ifPrepared { preparedState ->
                    val searchRequest = SearchRequest(
                        id = preparedState.data.firstOrNull()?.id?.plus(1) ?: 0,
                        request = searchText
                    )

                    val updatedSearchRequests = removeDuplicates(
                        sourceRequests = preparedState.data,
                        newSearchRequest = searchRequest
                    )
                    updatedSearchRequests.add(0, searchRequest)

                    if (updatedSearchRequests.size > sCachedRequestsLimit) {
                        reduceRequestsToLimit(updatedSearchRequests)
                    }

                    mSearchRequestsState.value = LoadState.Prepared(updatedSearchRequests)

                    mSearchRequestsLocalRepo.cacheSearchRequest(searchRequest)

                    if (toNetwork) {
                        mAuthenticationSource.getUserToken()?.let { userToken ->
                            mSearchRequestsRemoteRepo.cacheSearchRequest(userToken, searchRequest)
                        }
                    }
                }
            }
        }

    private suspend fun reduceRequestsToLimit(requests: MutableList<SearchRequest>) =
        withContext(mDispatchersProvider.IO) {
            val removedRequest = requests.removeLast()

            launch { erase(removedRequest) }
        }

    private suspend fun removeDuplicates(
        sourceRequests: List<SearchRequest>,
        newSearchRequest: SearchRequest
    ): MutableList<SearchRequest> = withContext(mDispatchersProvider.IO) {

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
        invalidateUserData(false)
        mSearchRequestsSyncer.invalidate()
    }

    override suspend fun onNetworkAvailable() {
        tryToSync()
    }

    override suspend fun onNetworkLost() {
        mSearchRequestsSyncer.invalidate()
    }

    override suspend fun clearUserData() {
        invalidateUserData(true)
    }

    private fun invalidateUserData(includingRemoteSource: Boolean) {
        mExternalScope.launch(mDispatchersProvider.IO) {
            mSearchRequestsLocalRepo.clearSearchRequests()

            if (includingRemoteSource) {
                mAuthenticationSource.getUserToken()?.let { userToken ->
                    mSearchRequestsRemoteRepo.clearSearchRequests(userToken)
                }
            }

            mSearchRequestsState.value = LoadState.Prepared(emptyList())
        }
    }

    private suspend fun erase(searchRequest: SearchRequest) {
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
                        Log.d("TEST", "New item $remoteSearchRequest")
                        cacheSearchRequest(remoteSearchRequest.request, false)
                    }
            }
        }
    }
}