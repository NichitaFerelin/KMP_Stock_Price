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

package com.ferelin.firebase

import com.ferelin.firebase.auth.FirebaseAuthenticator
import com.ferelin.firebase.database.favouriteCompanies.FavouriteCompaniesRef
import com.ferelin.firebase.database.searchRequests.SearchRequestsRef

/**
 * [FirebaseInteractor] represents an interface with variables that allows access firebase
 * services
 * */
interface FirebaseInteractor {

    /**
     * Provides methods for interacting with [FirebaseAuthenticator]
     * */
    val firebaseAuthenticator: FirebaseAuthenticator

    /**
     * Provides methods for interacting with user favourite companies that are stored
     * at firebase realtime database
     * */
    val favouriteCompaniesRef: FavouriteCompaniesRef

    /**
     * Provides methods for interacting with user search requests that are stored at
     * firebase realtime database
     * */
    val searchRequestsRef: SearchRequestsRef
}