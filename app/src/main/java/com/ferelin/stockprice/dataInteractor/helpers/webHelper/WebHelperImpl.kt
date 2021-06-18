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

package com.ferelin.stockprice.dataInteractor.helpers.webHelper

import com.ferelin.repository.Repository
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.dataManager.DataMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebHelperImpl @Inject constructor(
    private val mRepository: Repository,
    private val mDataMediator: DataMediator
) : WebHelper {

    override suspend fun openWebSocketConnection(): Flow<AdaptiveCompany> {
        return mRepository.openWebSocketConnection()
            .filter { it is RepositoryResponse.Success && it.owner != null }
            .onEach { mDataMediator.onLiveTimePriceChanged(it as RepositoryResponse.Success) }
            .map { mDataMediator.getCompany((it as RepositoryResponse.Success).owner!!)!! }
    }

    override fun prepareToWebSocketReconnection() {
        mRepository.closeWebSocketConnection()
        mDataMediator.subscribeItemsOnLiveTimeUpdates()
    }
}