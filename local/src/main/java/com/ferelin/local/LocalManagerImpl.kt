package com.ferelin.local

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

import com.ferelin.local.databases.chatsDb.ChatsDao
import com.ferelin.local.databases.companiesDb.CompaniesDao
import com.ferelin.local.databases.messagesDb.MessagesDao
import com.ferelin.local.databases.searchRequestsDb.SearchRequestsDao
import com.ferelin.local.json.JsonManager
import com.ferelin.local.models.Chat
import com.ferelin.local.models.Company
import com.ferelin.local.models.Message
import com.ferelin.local.models.SearchRequest
import com.ferelin.local.preferences.StorePreferences
import com.ferelin.local.responses.CompaniesResponse
import com.ferelin.local.responses.Responses
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class LocalManagerImpl @Inject constructor(
    private val mJsonManager: JsonManager,
    private val mStorePreferences: StorePreferences,
    private val mCompaniesDao: CompaniesDao,
    private val mMessagesDao: MessagesDao,
    private val mChatsDao: ChatsDao,
    private val mSearchRequestsDao: SearchRequestsDao
) : LocalManager {

    override suspend fun cacheCompany(company: Company) {
        mCompaniesDao.cacheCompany(company)
    }

    override suspend fun cacheAllCompanies(list: List<Company>) {
        mCompaniesDao.cacheAllCompanies(list)
    }

    override suspend fun updateCompany(company: Company) {
        mCompaniesDao.updateCompany(company)
    }

    /*
    * Empty room -> parsing from json
    * else -> return data from room
    * */
    override suspend fun getAllCompaniesAsResponse(): CompaniesResponse {
        val databaseCompanies = mCompaniesDao.getAllCompanies()
        return if (databaseCompanies.isEmpty()) {
            /*
            * If data companies loaded from room is empty -> than load companies from json assets
            * add cache it.
            * */
            val companiesFromJson = mJsonManager.getCompaniesFromJson()

            // Set ID for companies
            companiesFromJson.forEachIndexed { index, company -> company.id = index }

            // Save to database
            cacheAllCompanies(companiesFromJson)

            CompaniesResponse.Success(
                code = Responses.LOADED_FROM_JSON,
                companies = companiesFromJson
            )
        } else CompaniesResponse.Success(companies = databaseCompanies)
    }

    override suspend fun getAllCompanies(): List<Company> {
        return mCompaniesDao.getAllCompanies()
    }

    override suspend fun cacheMessage(message: Message) {
        mMessagesDao.cacheMessage(message)
    }

    override suspend fun getMessages(associatedUserNumber: String): List<Message>? {
        return mMessagesDao.getMessages(associatedUserNumber)
    }

    override fun clearMessages() {
        mMessagesDao.clearMessages()
    }

    override suspend fun getFirstTimeLaunchState(): Boolean? {
        return mStorePreferences.getFirstTimeLaunchState()
    }

    override suspend fun cacheFirstTimeLaunchState(boolean: Boolean) {
        mStorePreferences.cacheFirstTimeLaunchState(boolean)
    }

    override suspend fun cacheChat(chat: Chat) {
        mChatsDao.cacheChat(chat)
    }

    override suspend fun getAllChats(): List<Chat>? {
        return mChatsDao.getAllChats()
    }

    override fun clearChats() {
        mChatsDao.clearChats()
    }

    override suspend fun cacheUserNumber(number: String) {
        mStorePreferences.cacheUserNumber(number)
    }

    override suspend fun getUserNumber(): String? {
        return mStorePreferences.getUserNumber()
    }

    override suspend fun cacheSearchRequest(searchRequest: SearchRequest) {
        mSearchRequestsDao.cacheSearchRequest(searchRequest)
    }

    override suspend fun getAllSearchRequests(): List<SearchRequest> {
        return mSearchRequestsDao.getAllSearchRequests()
    }

    override suspend fun eraseSearchRequest(searchRequest: SearchRequest) {
        mSearchRequestsDao.eraseSearchRequest(searchRequest)
    }

    override fun clearSearchRequests() {
        mSearchRequestsDao.clearSearchRequests()
    }
}