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

package com.ferelin.firebase.auth

import android.app.Activity
import kotlinx.coroutines.flow.Flow

/**
 * Types of [FirebaseAuthenticator] responses
 * */
enum class AuthenticationResponse {
    CodeSent,
    Complete,
    EmptyPhone,
    TooManyRequests,
    Error
}

/**
 * Provides access for firebase authenticating by phone number
 */
interface FirebaseAuthenticator {

    /**
     * Provides authenticated user token
     *
     * @return current user identifier if user is logged, otherwise null
     * */
    val userToken: String?

    /**
     * Provides user authentication state
     *
     * @return user authentication state
     * */
    val isUserAuthenticated: Boolean

    /**
     * Sends a request for firebase to log into account
     *
     * @param holderActivity is used to build PhoneAuthOptions.
     * @param phone phone number to log in.
     * @return [AuthenticationResponse] object
     */
    fun tryToLogIn(holderActivity: Activity, phone: String): Flow<AuthenticationResponse>

    /**
     * Completes authentication with received code
     *
     * @param code is a code that was sent to the entered phone number
     */
    suspend fun completeAuthentication(code: String)

    /**
     * Logs out from firebase
     * */
    suspend fun logOut()
}