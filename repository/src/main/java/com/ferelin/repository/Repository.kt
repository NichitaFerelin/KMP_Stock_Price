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

import com.ferelin.repository.helpers.local.FavouriteCompaniesLocalHelper
import com.ferelin.repository.helpers.local.MessagesLocalHelper
import com.ferelin.repository.helpers.local.StorePreferencesHelper
import com.ferelin.repository.helpers.remote.api.ApiManagerHelper
import com.ferelin.repository.helpers.remote.auth.AuthenticationHelper
import com.ferelin.repository.helpers.remote.realtimeDatabase.FavouriteCompaniesRemoteHelper
import com.ferelin.repository.helpers.remote.realtimeDatabase.MessagesRemoteHelper
import com.ferelin.repository.helpers.remote.realtimeDatabase.SearchRequestsRemoteHelper
import com.ferelin.repository.helpers.remote.realtimeDatabase.UsersRemoteHelper
import com.ferelin.repository.helpers.remote.webSocket.WebSocketHelper

interface Repository :
    FavouriteCompaniesLocalHelper,
    MessagesLocalHelper,
    StorePreferencesHelper,
    ApiManagerHelper,
    AuthenticationHelper,
    FavouriteCompaniesRemoteHelper,
    MessagesRemoteHelper,
    SearchRequestsRemoteHelper,
    UsersRemoteHelper,
    WebSocketHelper