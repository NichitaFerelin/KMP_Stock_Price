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

import android.app.Activity
import com.ferelin.remote.auth.AuthenticationManager
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.utils.RepositoryMessages
import kotlinx.coroutines.flow.Flow

interface DataInteractor {

    suspend fun prepareData()

    suspend fun loadStockCandles(symbol: String): Flow<AdaptiveCompany>

    suspend fun loadCompanyNews(symbol: String): Flow<AdaptiveCompany>

    suspend fun loadCompanyQuote(
        symbol: String,
        position: Int,
        isImportant: Boolean = false
    ): Flow<AdaptiveCompany>

    suspend fun openConnection(): Flow<AdaptiveCompany>

    suspend fun addCompanyToFavourite(adaptiveCompany: AdaptiveCompany)

    suspend fun addCompanyToFavourite(symbol: String)

    suspend fun removeCompanyFromFavourite(adaptiveCompany: AdaptiveCompany)

    suspend fun removeCompanyFromFavourite(symbol: String)

    /**
     * @see [AuthenticationManager]
     */
    suspend fun signIn(holderActivity: Activity, phone: String): Flow<RepositoryMessages>

    /**
     * @see [AuthenticationManager]
     */
    fun logInWithCode(code: String)

    /**
     * @see [AuthenticationManager]
     */
    suspend fun logOut()

    suspend fun cacheNewSearchRequest(searchText: String)

    suspend fun setFirstTimeLaunchState(state: Boolean)

    suspend fun getFirstTimeLaunchState() : Boolean

    fun prepareToWebSocketReconnection()

    fun provideNetworkStateFlow(): Flow<Boolean>

    suspend fun findUser(login: String) : Boolean

    suspend fun tryToRegister(login: String): Flow<Boolean>

    suspend fun isUserRegistered() : Boolean

    fun isUserLogged() : Boolean
}