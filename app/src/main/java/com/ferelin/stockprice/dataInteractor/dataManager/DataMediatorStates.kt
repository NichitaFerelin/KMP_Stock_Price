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

package com.ferelin.stockprice.dataInteractor.dataManager

import com.ferelin.stockprice.dataInteractor.dataManager.workers.companies.CompaniesWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.errors.ErrorsWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.favouritesCompanies.FavouriteCompaniesWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.login.LoginWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.menuItems.MenuItemsWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.messages.MessagesWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.network.NetworkConnectivityWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.searchRequests.SearchRequestsWorkerStates

interface DataMediatorStates {

    val companiesWorker: CompaniesWorkerStates

    val favouriteCompaniesWorker: FavouriteCompaniesWorkerStates

    val searchRequestsWorker: SearchRequestsWorkerStates

    val menuItemsWorker: MenuItemsWorkerStates

    val networkConnectivityWorker: NetworkConnectivityWorkerStates

    val messagesWorker: MessagesWorkerStates

    val errorsWorker: ErrorsWorkerStates

    val loginWorker: LoginWorkerStates
}