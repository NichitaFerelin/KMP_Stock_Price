package com.ferelin.repository

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
import com.ferelin.remote.database.RealtimeDatabase
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.repository.utils.Time
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun loadStockCandles(
        symbol: String,
        /*
        * Api has a limit on receiving data a maximum of year ago
        * */
        from: Long = Time.convertMillisForRequest(System.currentTimeMillis() - Time.ONE_YEAR),
        to: Long = Time.convertMillisForRequest(System.currentTimeMillis()),
        // Time format
        resolution: String = "D"
    ): Flow<RepositoryResponse<AdaptiveCompanyHistory>>

    fun loadCompanyProfile(symbol: String): Flow<RepositoryResponse<AdaptiveCompanyProfile>>

    fun loadStockSymbols(): Flow<RepositoryResponse<AdaptiveStocksSymbols>>

    fun loadCompanyNews(
        symbol: String,
        /*
        * Api has a limit on receiving data a maximum of year ago
        * */
        from: String = Time.getYearAgoDateForRequest(),
        to: String = Time.getCurrentDateForRequest()
    ): Flow<RepositoryResponse<AdaptiveCompanyNews>>

    fun openWebSocketConnection(): Flow<RepositoryResponse<AdaptiveWebSocketPrice>>

    fun invalidateWebSocketConnection()

    fun loadCompanyQuote(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ): Flow<RepositoryResponse<AdaptiveCompanyDayData>>

    fun saveCompanyData(adaptiveCompany: AdaptiveCompany)

    fun getAllCompanies(): RepositoryResponse<List<AdaptiveCompany>>

    fun subscribeItemToLiveTimeUpdates(symbol: String, openPrice: Double)

    fun unsubscribeItemFromLiveTimeUpdates(symbol: String)

    suspend fun getSearchesHistory(): RepositoryResponse<List<AdaptiveSearchRequest>>

    suspend fun setSearchesHistory(requests: List<AdaptiveSearchRequest>)

    suspend fun getFirstTimeLaunchState(): RepositoryResponse<Boolean>

    suspend fun setFirstTimeLaunchState(state: Boolean)

    suspend fun clearSearchesHistory()

    /**
     * @see [AuthenticationManager]
     */
    fun tryToSignIn(
        holderActivity: Activity,
        phone: String
    ): Flow<RepositoryResponse<RepositoryMessages>>

    /**
     * @see [AuthenticationManager]
     */
    fun logInWithCode(code: String)

    /**
     * @see [AuthenticationManager]
     */
    fun provideUserId(): String?

    /**
     * @see [AuthenticationManager]
     */
    fun provideIsUserLogged(): Boolean

    /**
     * @see [AuthenticationManager]
     */
    fun logOut()

    /**
     * @see [RealtimeDatabase]
     */
    fun eraseCompanyFromRealtimeDb(userId: String, companyId: String)

    /**
     * @see [RealtimeDatabase]
     */
    fun writeCompanyIdToRealtimeDb(userId: String, companyId: String)

    /**
     * @see [RealtimeDatabase]
     */
    fun writeCompaniesIdsToDb(userId: String, companiesId: List<String>)

    /**
     * @see [RealtimeDatabase]
     */
    fun readCompaniesIdsFromDb(userId: String): Flow<RepositoryResponse<String?>>

    /**
     * @see [RealtimeDatabase]
     */
    fun writeSearchRequestToDb(userId: String, searchRequest: String)

    /**
     * @see [RealtimeDatabase]
     */
    fun writeSearchRequestsToDb(userId: String, searchRequests: List<String>)

    /**
     * @see [RealtimeDatabase]
     */
    fun readSearchRequestsFromDb(userId: String): Flow<RepositoryResponse<String?>>

    /**
     * @see [RealtimeDatabase]
     */
    fun eraseSearchRequestFromDb(userId: String, searchRequest: String)
}