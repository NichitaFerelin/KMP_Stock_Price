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

package com.ferelin.remote.database

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.database.helpers.chats.ChatsHelper
import com.ferelin.remote.database.helpers.favouriteCompanies.FavouriteCompaniesHelper
import com.ferelin.remote.database.helpers.messages.MessagesHelper
import com.ferelin.remote.database.helpers.searchRequests.SearchRequestsHelper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [RealtimeDatabaseImpl] is Firebase-Realtime-Database and is used to save
 * user data(such as favourite companies) in cloud.
 *
 *
 * Fore more info about methods look at [RealtimeDatabase]
 *
 * Data at cloud looks like:
 *
 *
 *      -FAVOURITE_COMPANIES
 *          -[userId1]
 *              -[companyId1]
 *              -[companyId2]
 *              -[companyId3]
 *          -[userId2]
 *              - ...
 *      -SEARCH_REQUESTS
 *          -[userId1]
 *              -[searchRequest1]
 *              -[searchRequest2]
 *              -[searchRequest3]
 */

@Singleton
class RealtimeDatabaseImpl @Inject constructor(
    private val mFavouriteCompaniesHelper: FavouriteCompaniesHelper,
    private val mSearchRequestsHelper: SearchRequestsHelper,
    private val mMessagesHelper: MessagesHelper,
    private val mChatsHelper: ChatsHelper
) : RealtimeDatabase {

    override fun eraseCompanyIdFromRealtimeDb(userId: String, companyId: String) {
        mFavouriteCompaniesHelper.eraseCompanyIdFromRealtimeDb(userId, companyId)
    }

    override fun writeCompanyIdToRealtimeDb(userId: String, companyId: String) {
        mFavouriteCompaniesHelper.writeCompanyIdToRealtimeDb(userId, companyId)
    }

    override fun writeCompaniesIdsToDb(userId: String, companiesId: List<String>) {
        mFavouriteCompaniesHelper.writeCompaniesIdsToDb(userId, companiesId)
    }

    override fun readCompaniesIdsFromDb(userId: String): Flow<BaseResponse<String?>> {
        return mFavouriteCompaniesHelper.readCompaniesIdsFromDb(userId)
    }

    override fun writeSearchRequestToDb(userId: String, searchRequest: String) {
        mSearchRequestsHelper.writeSearchRequestToDb(userId, searchRequest)
    }

    override fun writeSearchRequestsToDb(userId: String, searchRequests: List<String>) {
        mSearchRequestsHelper.writeSearchRequestsToDb(userId, searchRequests)
    }

    override fun readSearchRequestsFromDb(userId: String): Flow<BaseResponse<String?>> {
        return mSearchRequestsHelper.readSearchRequestsFromDb(userId)
    }

    override fun eraseSearchRequestFromDb(userId: String, searchRequest: String) {
        mSearchRequestsHelper.eraseSearchRequestFromDb(userId, searchRequest)
    }

    override fun cacheChat(currentUserNumber: String, associatedUserNumber: String) {
        mChatsHelper.cacheChat(currentUserNumber, associatedUserNumber)
    }

    override fun getUserChats(userNumber: String): Flow<BaseResponse<String>> {
        return mChatsHelper.getUserChats(userNumber)
    }

    override fun getMessagesForChat(
        currentUserNumber: String,
        associatedUserNumber: String
    ): Flow<BaseResponse<HashMap<String, Any>>> {
        return mMessagesHelper.getMessagesForChat(currentUserNumber, associatedUserNumber)
    }

    override fun cacheMessage(
        currentUserNumber: String,
        associatedUserNumber: String,
        messageText: String,
        messageSideKey: Char
    ) {
        mMessagesHelper.cacheMessage(
            currentUserNumber,
            associatedUserNumber,
            messageText,
            messageSideKey
        )
    }
}