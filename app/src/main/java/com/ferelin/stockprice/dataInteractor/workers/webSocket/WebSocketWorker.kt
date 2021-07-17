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

package com.ferelin.stockprice.dataInteractor.workers.webSocket

import com.ferelin.repository.Repository
import com.ferelin.repository.adaptiveModels.AdaptiveWebSocketPrice
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.workers.companies.CompaniesMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketWorker @Inject constructor(
    private val mRepository: Repository,
    private val mCompaniesMediator: CompaniesMediator
) {
    fun openWebSocketConnection(): Flow<RepositoryResponse<AdaptiveWebSocketPrice>> {
        return mRepository.openWebSocketConnection()
            .filter { it is RepositoryResponse.Success && it.owner != null }
            .onEach { mCompaniesMediator.onLiveTimePriceResponse(it as RepositoryResponse.Success) }
    }

    fun prepareToWebSocketReconnection() {
        mRepository.closeWebSocketConnection()
        mCompaniesMediator.subscribeItemsOnLiveTimeUpdates()
    }
}