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

import com.ferelin.remote.database.helpers.chats.ChatsHelper
import com.ferelin.remote.database.helpers.favouriteCompanies.FavouriteCompaniesHelper
import com.ferelin.remote.database.helpers.messages.MessagesHelper
import com.ferelin.remote.database.helpers.searchRequests.SearchRequestsHelper

/**
 * [RealtimeDatabase] provides ability to interact with realtime-database components.
 * [RealtimeDatabase] is Firebase-Realtime-Database and is used to save user
 * data(such as favourite companies) in cloud.
 *
 *  Data at cloud looks like:
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
 * */
interface RealtimeDatabase :
    FavouriteCompaniesHelper,
    SearchRequestsHelper,
    MessagesHelper,
    ChatsHelper