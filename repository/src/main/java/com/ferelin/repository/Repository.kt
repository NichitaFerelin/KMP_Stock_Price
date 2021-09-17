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

package com.ferelin.repository

import android.app.Activity
import com.ferelin.firebase.FirebaseInteractor
import com.ferelin.firebase.auth.FirebaseAuthenticator
import com.ferelin.firebase.database.favouriteCompanies.FavouriteCompaniesRef
import com.ferelin.firebase.database.searchRequests.SearchRequestsRef
import com.ferelin.local.LocalInteractor
import com.ferelin.local.dataStorage.DataStorage
import com.ferelin.local.database.CompaniesDao
import com.ferelin.local.jsonReader.AppJsonReader
import com.ferelin.remote.NetworkApiInteractor
import com.ferelin.remote.networkApi.NetworkApi
import com.ferelin.remote.webSocket.connector.WebSocketConnector
import com.ferelin.repository.useCaseModels.*
import com.ferelin.repository.utils.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

class Interactor constructor(
    override val companiesDao: CompaniesDao,
    override val dataStorage: DataStorage,
    override val jsonReader: AppJsonReader,
    override val networkApi: NetworkApi,
    override val webSocketConnector: WebSocketConnector,
    override val firebaseAuthenticator: FirebaseAuthenticator,
    override val favouriteCompaniesRef: FavouriteCompaniesRef,
    override val searchRequestsRef: SearchRequestsRef,
) : LocalInteractor, FirebaseInteractor, NetworkApiInteractor

@Singleton
class Repository @Inject constructor(private val mInteractor: Interactor) {

    suspend fun cacheCompany(company: UseCaseCompany) {
        mInteractor.companiesDao.cacheCompany(company.toDefaultCompany())
    }

    suspend fun cacheAllCompanies(list: List<UseCaseCompany>) {
        val defaultCompanies = list.map { it.toDefaultCompany() }
        mInteractor.companiesDao.cacheAllCompanies(defaultCompanies)
    }

    suspend fun getAllCompanies(): List<UseCaseCompany> {
        return mInteractor.companiesDao.getAllCompanies().map {
            it.toUseCaseCompany()
        }
    }

    suspend fun observeFirstTimeLaunch(): Flow<Boolean> {
        return mInteractor.dataStorage.observeFirstTimeLaunch()
    }

    suspend fun cacheFirstTimeLaunchState(value: Boolean) {
        mInteractor.dataStorage.cacheFirstTimeLaunchState(value)
    }

    suspend fun cacheSearchRequest(searchRequest: String) {
        mInteractor.dataStorage.cacheSearchRequest(searchRequest)
    }

    suspend fun observeSearchRequests(): Flow<Set<String>> {
        return mInteractor.dataStorage.observeSearchRequests()
    }

    suspend fun eraseSearchRequest(searchRequest: String) {
        mInteractor.dataStorage.eraseSearchRequest(searchRequest)

        // TODO call also
        /* suspend fun cacheSearchRequest(userToken: String, searchRequest: String) {
             mInteractor.searchRequestsRef.cacheSearchRequest(userToken, searchRequest)
         }

         suspend fun eraseSearchRequest(userToken: String, searchRequest: String) {
             mInteractor.searchRequestsRef.
         }*/
    }


    suspend fun clearSearchRequests() {
        mInteractor.dataStorage.clearSearchRequests()
    }

    suspend fun getCompaniesFromJson(): List<UseCaseCompany> {
        return mInteractor.jsonReader.getCompaniesFromJson().map {
            it.toUseCaseCompany()
        }
    }

    suspend fun loadPriceChangesHistory(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): RepositoryResponse<StockHistory> {
        return withResponseHandler(
            request = {
                mInteractor
                    .networkApi
                    .loadPriceChangesHistory(symbol, from, to, resolution)
            },
            mapper = { it.toStockHistory() }
        )
    }

    suspend fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): RepositoryResponse<CompanyNews> {
        return withResponseHandler(
            request = {
                mInteractor
                    .networkApi
                    .loadCompanyNews(symbol, from, to)
            },
            mapper = { it.toCompanyNews() }
        )
    }

    suspend fun loadActualStockPriceWithLimiter(
        symbol: String,
        keyPosition: Int,
        isImportant: Boolean
    ) {
        mInteractor.networkApi.loadActualStockPriceWithLimiter(symbol, keyPosition, isImportant)
    }

    fun observeActualStockPriceResponses(): Flow<RepositoryResponse<StockPrice>> {
        return mInteractor.networkApi.observeActualStockPriceResponses().map {
            unpackNetworkResponse(
                networkResponse = it,
                mapper = { it.toStockPrice() }
            )
        }
    }

    fun openConnection(): Flow<RepositoryResponse<LiveTimePrice>> {
        return mInteractor.webSocketConnector.openConnection().map {
            unpackNetworkResponse(
                networkResponse = it,
                mapper = { it.toLiveTimePrice() }
            )
        }
    }

    suspend fun closeConnection() {
        mInteractor.webSocketConnector.closeConnection()
    }

    suspend fun subscribe(symbol: String) {
        mInteractor.webSocketConnector.subscribe(symbol)
    }

    suspend fun unsubscribe(symbol: String) {
        mInteractor.webSocketConnector.unsubscribe(symbol)
    }

    fun getUserToken(): String? {
        return mInteractor.firebaseAuthenticator.userToken
    }

    fun isUserAuthenticated(): Boolean {
        return mInteractor.firebaseAuthenticator.isUserAuthenticated
    }

    fun tryToLogIn(holderActivity: Activity, phone: String): Flow<RepositoryMessages> {
        return mInteractor.firebaseAuthenticator.tryToLogIn(holderActivity, phone).map {
            it.toRepositoryMessage()
        }
    }

    suspend fun completeAuthentication(code: String) {
        mInteractor.firebaseAuthenticator.completeAuthentication(code)
    }

    suspend fun logOut() {
        mInteractor.firebaseAuthenticator.logOut()
    }

    suspend fun eraseFromFavourites(userToken: String, companyId: String) {
        mInteractor.favouriteCompaniesRef.eraseFromFavourites(userToken, companyId)
    }

    suspend fun cacheToFavourites(userToken: String, companyId: String) {
        mInteractor.favouriteCompaniesRef.cacheToFavourites(userToken, companyId)
    }

    suspend fun loadFavourites(userToken: String): List<String> {
        return mInteractor.favouriteCompaniesRef.loadFavourites(userToken)
    }

    suspend fun loadSearchRequests(userToken: String): List<String> {
        return mInteractor.searchRequestsRef.loadSearchRequests(userToken)
    }
}