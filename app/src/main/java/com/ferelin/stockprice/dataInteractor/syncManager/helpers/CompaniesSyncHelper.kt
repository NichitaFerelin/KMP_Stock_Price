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

package com.ferelin.stockprice.dataInteractor.syncManager.helpers

import com.ferelin.repository.RepositoryManagerHelper
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.dataInteractor.dataManager.DataMediator
import com.ferelin.stockprice.dataInteractor.syncManager.SyncConflictMode
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [CompaniesSyncHelper] holds logic about favourite companies synchronization
 * with realtime database.
 * @param mDataMediator is used to notify about favourite companies load/erase.
 * @param mRepositoryManager is used to work with realtime database
 * */
@Singleton
class CompaniesSyncHelper @Inject constructor(
    private val mDataMediator: DataMediator,
    private val mRepositoryManager: RepositoryManagerHelper
) {
    /*
    * Container for favourite companies which is at realtime database.
    * Directly is used to detect inconsistency between local and remote items.
    * */
    private var mRemoteCompaniesContainer = mutableListOf<AdaptiveCompany>()

    // User authentication ID
    private lateinit var mUserId: String

    // Synchronization mode to define how to sync data
    private lateinit var mSyncMode: SyncConflictMode


    /**
     * Prepare for companies synchronization with realtime database.
     * @param userId is a user authentication ID
     * @param syncMode is a synchronization mode of data. Is set to mode "merge" by default.
     */
    fun prepareForSync(userId: String, syncMode: SyncConflictMode = SyncConflictMode.Merge) {
        mUserId = userId
        mSyncMode = syncMode
        mRemoteCompaniesContainer.clear()
    }

    /**
     * Provides ability to notify about a new loaded favourite company from realtime database.
     *
     *
     * This synchronization step searches for a remote favourite company among local companies ->
     * depending on the 'isFavourite' field and [mSyncMode], the remote company
     * can be removed from real-time database or added to local database.
     * @param companyIdStr is a favourite company ID that was loaded and read from response.
     * */
    suspend fun onCompanyResponseSync(companyIdStr: String) {
        val remoteCompanyId = companyIdStr.toInt()
        val localCompanies = mDataMediator.companiesWorker.companies
        val localCompanyIndex = localCompanies.binarySearchBy(remoteCompanyId) { it.id }
        val localTargetCompany = mDataMediator.companiesWorker.companies[localCompanyIndex]

        when {
            localTargetCompany.isFavourite -> return

            mSyncMode is SyncConflictMode.LocalPriority -> {
                mRepositoryManager.eraseCompanyFromRealtimeDb(mUserId, companyIdStr)
            }

            else -> {
                mRemoteCompaniesContainer.add(localTargetCompany)
                mDataMediator.onAddFavouriteCompany(localTargetCompany)
            }
        }
    }

    /**
     * This method provides ability to notify real-time database that company has been removed
     * from favourites. Can be used directly when user removes company from favourites.
     *
     * Also it can be called while synchronization method is running. To avoid unnecessary
     * access to the real-time database -> use [mRemoteCompaniesContainer] before.
     */
    fun onCompanyRemovedFromLocal(company: AdaptiveCompany) {
        mRepositoryManager.provideUserId()?.let { authorizedUserId ->
            if (mRemoteCompaniesContainer.contains(company)) {
                mRemoteCompaniesContainer.remove(company)
                mRepositoryManager.eraseCompanyFromRealtimeDb(
                    authorizedUserId,
                    company.id.toString()
                )
            }
        }
    }

    /**
     * Provides ability to notify real-time database that company has been added to favourites.
     * Can be used directly when user add company to favourites.
     *
     * Also it can be called while synchronization method is running. To avoid unnecessary
     * access to the real-time database -> use [mRemoteCompaniesContainer] before.
     */
    fun onCompanyAddedToLocal(company: AdaptiveCompany) {
        mRepositoryManager.provideUserId()?.let { authorizedUserId ->
            if (!mRemoteCompaniesContainer.contains(company)) {
                mRemoteCompaniesContainer.add(company)
                mRepositoryManager.writeCompanyIdToRealtimeDb(
                    mRepositoryManager.provideUserId()!!,
                    company.id.toString()
                )
            }
        }
    }

    /**
     * Final step on synchronization.
     * This step is based on [mSyncMode] and [mRemoteCompaniesContainer]
     * and is not required if sync mode is with Remote Priority.
     *
     * @see detectInconsistencyAndSync
     * */
    fun onSyncEnd() {
        if (mSyncMode !is SyncConflictMode.RemotePriority) {
            detectInconsistencyAndSync()
        }
    }

    /**
     * When the user exits need to call this method to clear his data
     */
    suspend fun onLogOut() {
        val localFavouriteCompanies =
            mDataMediator.favouriteCompaniesWorker.stateFavouriteCompanies.value.data!!.toList()
        localFavouriteCompanies.forEach { mDataMediator.onRemoveFavouriteCompany(it) }
    }

    /**
     * On this synchronization step [mRemoteCompaniesContainer] is completely filled with data
     * from real-time database. Now need to find missing local favourite companies which was not
     * added to real-time database.
     * */
    private fun detectInconsistencyAndSync() {
        val localFavouriteCompanies =
            mDataMediator.favouriteCompaniesWorker.stateFavouriteCompanies.value.data!!

        if (mRemoteCompaniesContainer.isEmpty()) {
            val ids = localFavouriteCompanies.map { it.id.toString() }
            mRepositoryManager.writeCompaniesIdsToDb(mUserId, ids)
        } else {
            localFavouriteCompanies.forEach { localCompany ->
                val id = localCompany.id
                val indexAtRemoteContainer = mRemoteCompaniesContainer.binarySearchBy(id) { it.id }
                if (indexAtRemoteContainer < 0) {
                    mRepositoryManager.writeCompanyIdToRealtimeDb(mUserId, id.toString())
                }
            }
        }
    }
}