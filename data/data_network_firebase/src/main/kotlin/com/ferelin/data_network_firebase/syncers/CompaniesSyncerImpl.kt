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

import com.ferelin.domain.repositories.companies.CompaniesRemoteRepo
import com.ferelin.domain.syncers.CompaniesSyncer
import com.ferelin.data_network_firebase.utils.itemsNotIn
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.ifPrepared
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompaniesSyncerImpl @Inject constructor(
    private val companiesRemoteRepo: CompaniesRemoteRepo,
    private val dispatchersProvider: DispatchersProvider,
) : CompaniesSyncer {

    private var isDataSynchronized: Boolean = false

    override suspend fun initDataSync(
        userToken: String,
        sourceCompaniesIds: List<Int>
    ): List<Int> {
        Timber.d(
            "init data sync (isSynchronized = $isDataSynchronized, " +
                    "source companies size = ${sourceCompaniesIds.size}"
        )

        if (isDataSynchronized) {
            return emptyList()
        }

        val remoteCompaniesState = withContext(dispatchersProvider.IO) {
            companiesRemoteRepo.loadAll(userToken).firstOrNull()
        }

        return remoteCompaniesState?.ifPrepared { preparedState ->
            val remoteCompaniesIds = preparedState.data
            syncCloudDb(userToken, sourceCompaniesIds, remoteCompaniesIds)

            remoteCompaniesIds.itemsNotIn(sourceCompaniesIds)
        } ?: emptyList()
    }

    override fun invalidate() {
        Timber.d("invalidate")
        isDataSynchronized = false
    }

    private suspend fun syncCloudDb(
        userToken: String,
        sourceCompaniesIds: List<Int>,
        remoteCompaniesIds: List<Int>
    ): Unit = withContext(dispatchersProvider.IO) {

        sourceCompaniesIds
            .itemsNotIn(remoteCompaniesIds)
            .forEach { companiesRemoteRepo.insertBy(userToken, it) }

        isDataSynchronized = true
    }
}