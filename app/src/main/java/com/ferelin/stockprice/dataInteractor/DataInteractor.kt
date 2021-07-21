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

import com.ferelin.stockprice.dataInteractor.interactorHelpers.*
import com.ferelin.stockprice.dataInteractor.workers.authentication.AuthenticationWorkerStates
import com.ferelin.stockprice.dataInteractor.workers.chats.ChatsWorkerStates
import com.ferelin.stockprice.dataInteractor.workers.companies.defaults.CompaniesWorkerStates
import com.ferelin.stockprice.dataInteractor.workers.companies.favourites.FavouriteCompaniesWorkerStates
import com.ferelin.stockprice.dataInteractor.workers.errors.ErrorsWorkerStates
import com.ferelin.stockprice.dataInteractor.workers.menuItems.MenuItemsWorkerStates
import com.ferelin.stockprice.dataInteractor.workers.messages.MessagesWorkerStates
import com.ferelin.stockprice.dataInteractor.workers.network.NetworkConnectivityWorkerStates
import com.ferelin.stockprice.dataInteractor.workers.searchRequests.SearchRequestsWorkerStates
import com.ferelin.stockprice.utils.StockHistoryConverter
import kotlinx.coroutines.flow.Flow

/**
 * DataInteractor is an entity with which the UI layer can interact with data,
 * execute requests to the network, receive information about errors from data states
 * and etc.
 * */
interface DataInteractor :
// States
    AuthenticationWorkerStates,
    CompaniesWorkerStates,
    FavouriteCompaniesWorkerStates,
    ErrorsWorkerStates,
    MenuItemsWorkerStates,
    ChatsWorkerStates,
    MessagesWorkerStates,
    NetworkConnectivityWorkerStates,
    SearchRequestsWorkerStates,
    // Helpers
    AuthenticationHelper,
    CompaniesHelper,
    ChatsHelper,
    SearchRequestsHelper,
    WebSocketHelper,
    MessagesHelper,
    FirstTimeLaunchHelper,
    UserNumberHelper {

    val stockHistoryConverter: StockHistoryConverter

    fun provideNetworkStateFlow(): Flow<Boolean>
}