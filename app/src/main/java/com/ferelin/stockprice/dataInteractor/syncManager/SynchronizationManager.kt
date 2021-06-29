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

package com.ferelin.stockprice.dataInteractor.syncManager

import com.ferelin.repository.Repository
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.syncManager.helpers.CompaniesSyncHelper
import com.ferelin.stockprice.dataInteractor.syncManager.helpers.SearchRequestsSyncHelper
import com.ferelin.stockprice.utils.actionHolder.ActionHolder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [SynchronizationManager] is responsible for data synchronization between local and real-time
 * database.
 *
 * @param mCoroutineContextProvider is coroutine context provider for coroutine launch.
 * @param mCompaniesSyncHelper is a synchronization helper for favourite companies.
 * @param mSearchRequestsSyncHelper is a synchronization helper for search requests.
 * @param mRepository is used to work with real-time database.
 */
@Singleton
class SynchronizationManager @Inject constructor(
    private val mCoroutineContextProvider: CoroutineContextProvider,
    private val mCompaniesSyncHelper: CompaniesSyncHelper,
    private val mSearchRequestsSyncHelper: SearchRequestsSyncHelper,
    private val mRepository: Repository,
    private val mAppScope: CoroutineScope
) {

    /*
    * Synchronization jobs
    * */
    private var mSyncJob: Job? = null
    private var mCompaniesSyncJob: Job? = null
    private var mSearchRequestsSyncJob: Job? = null

    private var mIsDataSynchronized = false

    /**
     * @see [CompaniesSyncHelper]
     * */
    fun onCompanyRemovedFromLocal(company: AdaptiveCompany) {
        mCompaniesSyncHelper.onCompanyRemovedFromLocal(company)
    }

    /**
     * @see [CompaniesSyncHelper]
     * */
    fun onCompanyAddedToLocal(company: AdaptiveCompany) {
        mCompaniesSyncHelper.onCompanyAddedToLocal(company)
    }

    /**
     * @see [SearchRequestsSyncHelper]
     * */
    fun onSearchRequestsChanged(changesActionsHistory: List<ActionHolder<String>>) {
        mSearchRequestsSyncHelper.onSearchRequestsChanged(changesActionsHistory)
    }

    /**
     * Synchronization is depending on network.
     * When network has been lost synchronization must be canceled.
     * When network is available and data is not synchronized need to synchronize it.
     * */
    fun onNetworkStateChanged(isAvailable: Boolean) {
        when {
            !isAvailable -> invalidateSynchronization()
            !mIsDataSynchronized -> initDataSync()
        }
    }

    /**
     * Synchronizes data when user log in.
     */
    fun onLogIn() {
        initDataSync()
    }

    /**
     * Invalidates synchronization when user log out.
     * @see [CompaniesSyncHelper]
     * @see [SearchRequestsSyncHelper]
     * */
    suspend fun onLogOut() {
        invalidateSynchronization()
        mCompaniesSyncHelper.onLogOut()
        mSearchRequestsSyncHelper.onLogOut()
    }

    /**
     * Provides ability to synchronize data. Starts job that collecting data from real-time database
     * and notifies sync-helpers about each item from remote.
     *
     * @param syncMode is a synchronization mode
     * */
    private fun initDataSync(syncMode: SyncConflictMode = SyncConflictMode.Merge) {
        if (!mIsDataSynchronized && !mRepository.isUserAuthenticated()) {
            return
        }

        val userId = mRepository.getUserAuthenticationId()!!
        mCompaniesSyncHelper.prepareForSync(userId, syncMode)
        mSearchRequestsSyncHelper.prepareToSync(userId, syncMode)

        mAppScope.launch {
            mSyncJob = launch {
                collectCompaniesIds(this, userId)
                collectSearchRequests(this, userId)

                launch {
                    /*
                    * Waits while synchronization will be done and stop all jobs.
                    * */
                    mSearchRequestsSyncJob!!.join()
                    mCompaniesSyncJob!!.join()
                    if (isActive) {
                        invalidateSynchronization()
                        mIsDataSynchronized = true
                    }
                }
            }
        }
    }

    /**
     * Stops all sync jobs
     * */
    private fun invalidateSynchronization() {
        mSyncJob?.cancel()
        mSyncJob = null
        mCompaniesSyncJob = null
        mSearchRequestsSyncJob = null
        mIsDataSynchronized = false
    }

    private fun collectCompaniesIds(scope: CoroutineScope, userId: String) {
        mCompaniesSyncJob = scope.launch {
            mRepository.getCompaniesIdsFromRealtimeDb(userId).collect { response ->
                if (isActive) {
                    when (response) {
                        is RepositoryResponse.Success -> {
                            mCompaniesSyncHelper.onCompanyResponseSync(response.data!!)
                        }
                        is RepositoryResponse.Failed -> {
                            mCompaniesSyncHelper.onSyncEnd()
                            cancel()
                        }
                    }
                }
            }
        }
    }

    private fun collectSearchRequests(scope: CoroutineScope, userId: String) {
        mSearchRequestsSyncJob = scope.launch {
            mRepository.getSearchRequestsFromRealtimeDb(userId).collect { response ->
                if (isActive) {
                    when (response) {
                        is RepositoryResponse.Success -> {
                            mSearchRequestsSyncHelper.onSearchRequestResponseSync(response.data!!)
                        }
                        is RepositoryResponse.Failed -> {
                            mSearchRequestsSyncHelper.onSyncEnd()
                            cancel()
                        }
                    }
                }
            }
        }
    }
}