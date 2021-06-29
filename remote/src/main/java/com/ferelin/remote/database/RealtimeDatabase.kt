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

import com.ferelin.remote.database.helpers.favouriteCompanies.FavouriteCompaniesHelper
import com.ferelin.remote.database.helpers.messages.MessagesHelper
import com.ferelin.remote.database.helpers.relations.RelationsHelper
import com.ferelin.remote.database.helpers.searchRequests.SearchRequestsHelper
import com.ferelin.remote.database.helpers.user.UsersHelper

interface RealtimeDatabase :
    FavouriteCompaniesHelper,
    SearchRequestsHelper,
    UsersHelper,
    MessagesHelper,
    RelationsHelper {

    companion object {

        /**
         * Firebase Database paths must not contain '.', '#', '$', '[', ']'.
         */
        private const val sUnavailableSymbolsPattern = "[.#$\\[\\]]"

        fun isTextAvailableForFirebase(text: String): Boolean {
            return !text.contains(Regex(sUnavailableSymbolsPattern))
        }

        /*
        * Encrypts to avoid exceptions
        * */
        fun encrypt(str: String): String {
            return str.replace(Regex(sUnavailableSymbolsPattern), "%")
        }

        /*
        * Decrypts for repository
        * */
        fun decrypt(str: String?): String? {
            return str?.replace('%', '.')
        }
    }
}