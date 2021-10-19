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

package com.ferelin.domain.sources

import android.app.Activity
import kotlinx.coroutines.flow.Flow

enum class AuthResponse {
    EmptyPhone,
    PhoneProcessing,
    CodeSent,
    CodeProcessing,
    Complete,
    TooManyRequests,
    Error
}

interface AuthenticationSource {

    /**
     * The main request for authentication which next returns all responses from the server
     * @param holderActivity is an activity which is used to check for a robot
     * @param phone is an phone number by which need to authorize
     * @return flow with [AuthResponse] states
     * */
    fun tryToLogIn(holderActivity: Activity, phone: String): Flow<AuthResponse>

    fun isUserAuthenticated() : Boolean

    fun getCodeRequiredSize() : Int

    fun getUserToken() : String?

    suspend fun completeAuthentication(code: String)

    suspend fun logOut()
}