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

    override suspend fun insertCompany(company: Company) {
        mCompaniesDao.insertCompany(company)
    }

    override suspend fun insertAllCompanies(list: List<Company>) {
        mCompaniesDao.insertAllCompanies(list)
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
            insertAllCompanies(companiesFromJson)

            CompaniesResponse.Success(
                code = Responses.LOADED_FROM_JSON,
                companies = companiesFromJson
            )
        } else CompaniesResponse.Success(companies = databaseCompanies)
    }

    override suspend fun getAllCompanies(): List<Company> {
        return mCompaniesDao.getAllCompanies()
    }

    override suspend fun insertMessage(message: Message) {
        mMessagesDao.insertMessage(message)
    }

    override suspend fun getMessages(associatedUserNumber: String): List<Message>? {
        return mMessagesDao.getMessages(associatedUserNumber)
    }

    override fun clearMessagesTable() {
        mMessagesDao.clearMessagesTable()
    }

    override suspend fun getFirstTimeLaunchState(): Boolean? {
        return mStorePreferences.getFirstTimeLaunchState()
    }

    override suspend fun setFirstTimeLaunchState(boolean: Boolean) {
        mStorePreferences.setFirstTimeLaunchState(boolean)
    }

    override suspend fun insertChat(chat: Chat) {
        mChatsDao.insertChat(chat)
    }

    override suspend fun getAllChats(): List<Chat>? {
        return mChatsDao.getAllChats()
    }

    override fun clearChats() {
        mChatsDao.clearChats()
    }

    override suspend fun setUserNumber(number: String) {
        mStorePreferences.setUserNumber(number)
    }

    override suspend fun getUserNumber(): String? {
        return mStorePreferences.getUserNumber()
    }

    override suspend fun insertSearchRequest(searchRequest: SearchRequest) {
        mSearchRequestsDao.insertSearchRequest(searchRequest)
    }

    override suspend fun getAllSearchRequests(): List<SearchRequest> {
        return mSearchRequestsDao.getAllSearchRequests()
    }

    override suspend fun eraseSearchRequest(searchRequest: SearchRequest) {
        mSearchRequestsDao.eraseSearchRequest(searchRequest)
    }

    override fun clearSearchRequestsTable() {
        mSearchRequestsDao.clearSearchRequestsTable()
    }
}