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

import com.ferelin.domain.repositories.companies.CompaniesLoadState
import com.ferelin.domain.repositories.companies.CompaniesRemoteRepo
import com.ferelin.domain.syncers.CompaniesSyncer
import com.ferelin.firebase.utils.itemsNotIn
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompaniesSyncerImpl @Inject constructor(
    private val mCompaniesRemoteRepo: CompaniesRemoteRepo,
    private val mDispatchersProvider: DispatchersProvider,
) : CompaniesSyncer {

    private var mIsDataSynchronized: Boolean = false

    override suspend fun initDataSync(
        userToken: String,
        sourceCompaniesIds: List<Int>
    ): List<Int> {
        Timber.d(
            "init sync (isSynchronized = $mIsDataSynchronized, " +
                    "localSize = ${sourceCompaniesIds.size}"
        )

        if (mIsDataSynchronized) {
            return emptyList()
        }

        val remoteCompaniesState = withContext(mDispatchersProvider.IO) {
            mCompaniesRemoteRepo.getFavouriteCompaniesIds(userToken).firstOrNull()
        }

        Timber.d("loaded remote companies state = $remoteCompaniesState")

        return if (remoteCompaniesState is CompaniesLoadState.Loaded) {
            val remoteCompaniesIds = remoteCompaniesState.companies
            syncCloudDb(userToken, sourceCompaniesIds, remoteCompaniesIds)

            remoteCompaniesIds.itemsNotIn(sourceCompaniesIds)
        } else {
            emptyList()
        }
    }

    override fun invalidate() {
        Timber.d("invalidate")
        mIsDataSynchronized = false
    }

    private suspend fun syncCloudDb(
        userToken: String,
        sourceCompaniesIds: List<Int>,
        remoteCompaniesIds: List<Int>
    ): Unit = withContext(mDispatchersProvider.IO) {
        Timber.d(
            "sync cloud db (sources = ${sourceCompaniesIds.size}, " +
                    "remotes = ${remoteCompaniesIds.size})"
        )
        sourceCompaniesIds
            .itemsNotIn(remoteCompaniesIds)
            .forEach { mCompaniesRemoteRepo.cacheCompanyIdToFavourites(userToken, it) }

        mIsDataSynchronized = true
    }
}