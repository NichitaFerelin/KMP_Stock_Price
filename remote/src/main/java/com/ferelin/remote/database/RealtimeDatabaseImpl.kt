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

@Singleton
class RealtimeDatabaseImpl @Inject constructor(
    private val mFavouriteCompaniesHelper: FavouriteCompaniesHelper,
    private val mSearchRequestsHelper: SearchRequestsHelper,
    private val mMessagesHelper: MessagesHelper,
    private val mChatsHelper: ChatsHelper
) : RealtimeDatabase {

    override fun eraseCompanyIdFromRealtimeDb(userToken: String, companyId: String) {
        mFavouriteCompaniesHelper.eraseCompanyIdFromRealtimeDb(userToken, companyId)
    }

    override fun cacheCompanyIdToRealtimeDb(userToken: String, companyId: String) {
        mFavouriteCompaniesHelper.cacheCompanyIdToRealtimeDb(userToken, companyId)
    }

    override fun getCompaniesIdsFromDb(userToken: String): Flow<BaseResponse<List<String>>> {
        return mFavouriteCompaniesHelper.getCompaniesIdsFromDb(userToken)
    }

    override fun cacheSearchRequestToDb(
        userToken: String,
        searchRequestId: String,
        searchRequest: String
    ) {
        mSearchRequestsHelper.cacheSearchRequestToDb(userToken, searchRequestId, searchRequest)
    }

    override fun getSearchRequestsFromDb(userToken: String): Flow<BaseResponse<HashMap<Int, String>>> {
        return mSearchRequestsHelper.getSearchRequestsFromDb(userToken)
    }

    override fun eraseSearchRequestFromDb(userToken: String, searchRequestId: String) {
        mSearchRequestsHelper.eraseSearchRequestFromDb(userToken, searchRequestId)
    }

    override fun cacheChat(
        chatId: String,
        currentUserNumber: String,
        associatedUserNumber: String
    ) {
        mChatsHelper.cacheChat(chatId, currentUserNumber, associatedUserNumber)
    }

    override fun getChatsByUserNumber(userNumber: String): Flow<BaseResponse<String>> {
        return mChatsHelper.getChatsByUserNumber(userNumber)
    }

    override fun getMessagesForChat(
        currentUserNumber: String,
        associatedUserNumber: String
    ): Flow<BaseResponse<HashMap<String, Any>>> {
        return mMessagesHelper.getMessagesForChat(currentUserNumber, associatedUserNumber)
    }

    override fun cacheMessage(
        messageId: String,
        currentUserNumber: String,
        associatedUserNumber: String,
        messageText: String,
        messageSideKey: Char
    ) {
        mMessagesHelper.cacheMessage(
            messageId,
            currentUserNumber,
            associatedUserNumber,
            messageText,
            messageSideKey
        )
    }
}