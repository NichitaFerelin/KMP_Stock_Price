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

package com.ferelin.stockprice.dataInteractor.helpers.apiHelper

import com.ferelin.repository.Repository
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.dataManager.DataMediator
import com.ferelin.stockprice.dataInteractor.dataManager.workers.network.NetworkConnectivityWorkerStates
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiHelperImpl @Inject constructor(
    private val mRepository: Repository,
    private val mDataMediator: DataMediator,
    private val mNetworkConnectivityWorkerStates: NetworkConnectivityWorkerStates
) : ApiHelper {

    private val mStateIsNetworkAvailable: StateFlow<Boolean>
        get() = mNetworkConnectivityWorkerStates.stateIsNetworkAvailable

    override suspend fun loadStockCandlesFromNetwork(symbol: String): Flow<AdaptiveCompany> {
        return mRepository.loadStockCandles(symbol)
            .onEach { repositoryResponse ->
                when (repositoryResponse) {
                    is RepositoryResponse.Success -> {
                        mDataMediator.onStockCandlesLoaded(repositoryResponse)
                    }
                    is RepositoryResponse.Failed -> {
                        if (mStateIsNetworkAvailable.value) {
                            mDataMediator.onLoadStockCandlesError(
                                repositoryResponse.message,
                                symbol
                            )
                        }
                    }
                }
            }
            .filter { it is RepositoryResponse.Success }
            .map { mDataMediator.getCompany(symbol)!! }
    }

    override suspend fun loadCompanyNewsFromNetwork(symbol: String): Flow<AdaptiveCompany> {
        return mRepository.loadCompanyNews(symbol)
            .onEach {
                when (it) {
                    is RepositoryResponse.Success -> mDataMediator.onCompanyNewsLoaded(it)
                    is RepositoryResponse.Failed -> {
                        if (mStateIsNetworkAvailable.value) {
                            mDataMediator.onLoadCompanyNewsError(
                                it.message,
                                symbol
                            )
                        }
                    }
                }
            }
            .filter { it is RepositoryResponse.Success }
            .map { mDataMediator.getCompany(symbol)!! }
    }

    override suspend fun loadCompanyQuoteFromNetwork(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ): Flow<AdaptiveCompany> {
        return mRepository.loadCompanyQuote(symbol, position, isImportant)
            .filter { it is RepositoryResponse.Success && it.owner != null }
            .onEach { mDataMediator.onCompanyQuoteLoaded(it as RepositoryResponse.Success) }
            .map { mDataMediator.getCompany((it as RepositoryResponse.Success).owner!!)!! }
    }
}