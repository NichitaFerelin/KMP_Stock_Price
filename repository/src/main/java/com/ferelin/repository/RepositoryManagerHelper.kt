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
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.repository.utils.Time
import kotlinx.coroutines.flow.Flow
import com.ferelin.remote.auth.AuthenticationManagerHelper
import com.ferelin.remote.database.RealtimeDatabaseHelper

interface RepositoryManagerHelper {

    fun loadStockCandles(
        symbol: String,
        /*
        * Api has a limit on receiving data a maximum of year ago
        * */
        from: Long = Time.convertMillisForRequest(System.currentTimeMillis() - Time.ONE_YEAR),
        to: Long = Time.convertMillisForRequest(System.currentTimeMillis()),
        // Days format
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

    fun getAllCompanies(): Flow<RepositoryResponse<List<AdaptiveCompany>>>

    fun subscribeItemToLiveTimeUpdates(symbol: String, openPrice: Double)

    fun unsubscribeItemFromLiveTimeUpdates(symbol: String)

    fun getSearchesHistory(): Flow<RepositoryResponse<List<AdaptiveSearchRequest>>>

    suspend fun setSearchesHistory(requests: List<AdaptiveSearchRequest>)

    fun getFirstTimeLaunchState(): Flow<RepositoryResponse<Boolean>>

    suspend fun setFirstTimeLaunchState(state: Boolean)

    suspend fun clearSearchesHistory()

    /**
     * @see [AuthenticationManagerHelper]
     */
    fun tryToSignIn(
        holderActivity: Activity,
        phone: String
    ): Flow<RepositoryResponse<RepositoryMessages>>

    /**
     * @see [AuthenticationManagerHelper]
     */
    fun logInWithCode(code: String)

    /**
     * @see [AuthenticationManagerHelper]
     */
    fun provideUserId(): String?

    /**
     * @see [AuthenticationManagerHelper]
     */
    fun provideIsUserLogged(): Boolean

    /**
     * @see [AuthenticationManagerHelper]
     */
    fun logOut()

    /**
     * @see [RealtimeDatabaseHelper]
     */
    fun eraseCompanyFromRealtimeDb(userId: String, companyId: String)

    /**
     * @see [RealtimeDatabaseHelper]
     */
    fun writeCompanyIdToRealtimeDb(userId: String, companyId: String)

    /**
     * @see [RealtimeDatabaseHelper]
     */
    fun writeCompaniesIdsToDb(userId: String, companiesId: List<String>)

    /**
     * @see [RealtimeDatabaseHelper]
     */
    fun readCompaniesIdsFromDb(userId: String): Flow<RepositoryResponse<String?>>

    /**
     * @see [RealtimeDatabaseHelper]
     */
    fun writeSearchRequestToDb(userId: String, searchRequest: String)

    /**
     * @see [RealtimeDatabaseHelper]
     */
    fun writeSearchRequestsToDb(userId: String, searchRequests: List<String>)

    /**
     * @see [RealtimeDatabaseHelper]
     */
    fun readSearchRequestsFromDb(userId: String): Flow<RepositoryResponse<String?>>

    /**
     * @see [RealtimeDatabaseHelper]
     */
    fun eraseSearchRequestFromDb(userId: String, searchRequest: String)
}