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
import com.ferelin.remote.database.helpers.favouriteCompaniesHelper.FavouriteCompaniesHelper
import com.ferelin.remote.database.helpers.messagesHelper.MessagesHelper
import com.ferelin.remote.database.helpers.searchRequestsHelper.SearchRequestsHelper
import com.ferelin.remote.database.helpers.userHelper.UsersHelper
import com.ferelin.shared.MessageSide
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
    private val mUsersHelper: UsersHelper,
    private val mMessagesHelper: MessagesHelper
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

    override fun findUserByLogin(login: String): Flow<Boolean> {
        return mUsersHelper.findUserByLogin(login)
    }

    override fun findUserById(userId: String): Flow<Boolean> {
        return mUsersHelper.findUserById(userId)
    }

    override suspend fun tryToRegister(userId: String, login: String): Flow<BaseResponse<Boolean>> {
        return mUsersHelper.tryToRegister(userId, login)
    }

    override fun addNewRelation(sourceUserLogin: String, secondSideUserLogin: String) {
        mMessagesHelper.addNewRelation(sourceUserLogin, secondSideUserLogin)
    }

    override fun getUserRelations(userLogin: String): Flow<BaseResponse<List<String>>> {
        return mMessagesHelper.getUserRelations(userLogin)
    }

    override fun getMessagesAssociatedWithSpecifiedUser(
        sourceUserLogin: String,
        secondSideUserLogin: String
    ): Flow<BaseResponse<List<Pair<Char, String>>>> {
        return mMessagesHelper.getMessagesAssociatedWithSpecifiedUser(
            sourceUserLogin,
            secondSideUserLogin
        )
    }

    override fun addNewMessage(
        sourceUserLogin: String,
        secondSideUserLogin: String,
        messageId: String,
        message: String,
        side: MessageSide
    ) {
        mMessagesHelper.addNewMessage(
            sourceUserLogin,
            secondSideUserLogin,
            messageId,
            message,
            side
        )
    }
}