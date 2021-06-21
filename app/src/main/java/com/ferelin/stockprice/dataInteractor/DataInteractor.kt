package com.ferelin.stockprice.dataInteractor

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

import com.ferelin.stockprice.dataInteractor.dataManager.workers.authentication.AuthenticationWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.companies.defaults.CompaniesWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.companies.favourites.FavouriteCompaniesWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.errors.ErrorsWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.menuItems.MenuItemsWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.messages.MessagesWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.network.NetworkConnectivityWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.relations.RelationsWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.searchRequests.SearchRequestsWorkerStates
import com.ferelin.stockprice.dataInteractor.interactorHelpers.*
import com.ferelin.stockprice.utils.StockHistoryConverter
import kotlinx.coroutines.flow.Flow

interface DataInteractor :
// States
    CompaniesWorkerStates,
    FavouriteCompaniesWorkerStates,
    ErrorsWorkerStates,
    AuthenticationWorkerStates,
    MenuItemsWorkerStates,
    RelationsWorkerStates,
    MessagesWorkerStates,
    NetworkConnectivityWorkerStates,
    SearchRequestsWorkerStates,
    // Helpers
    AuthenticationHelper,
    CompaniesHelper,
    RegisterHelper,
    RelationsHelper,
    SearchRequestsHelper,
    WebSocketHelper,
    MessagesHelper,
    FirstTimeLaunchHelper {

    val stockHistoryConverter: StockHistoryConverter

    suspend fun prepareData()

    fun provideNetworkStateFlow(): Flow<Boolean>
}