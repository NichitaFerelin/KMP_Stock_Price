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

package com.ferelin.domain.interactors

import android.app.Activity
import com.ferelin.domain.sources.AuthResponse
import com.ferelin.domain.sources.AuthenticationSource
import com.ferelin.shared.AuthenticationListener
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

/**
 * [AuthenticationInteractor] allows to authenticate user
 * */
class AuthenticationInteractor @Inject constructor(
    private val authenticationSource: AuthenticationSource,
    private val dispatchersProvider: DispatchersProvider,
    private val authenticationListeners: List<@JvmSuppressWildcards AuthenticationListener>,
    @Named("ExternalScope") private val externalScope: CoroutineScope
) {
    /**
     * The main request for authentication which next returns all responses from the server
     * @param holderActivity is an activity which is used to check for a robot
     * @param phone is an phone number by which need to authorize
     * @return flow with [AuthResponse] states
     * */
    fun tryToLogIn(holderActivity: Activity, phone: String): Flow<AuthResponse> {
        return authenticationSource.tryToLogIn(holderActivity, phone)
            .onEach { notifyIfCompleted(it) }
    }

    /**
     * When the code is sent to user this method can be used to complete authentication by
     * received code
     * @param code is a received code
     * */
    suspend fun completeAuthentication(code: String) {
        authenticationSource.completeAuthentication(code)
    }

    /**
     * User log out
     * */
    suspend fun logOut() {
        externalScope.launch(dispatchersProvider.IO) {
            authenticationSource.logOut()

            // Notification for all listeners
            authenticationListeners.forEach {
                it.onLogOut()
            }
        }
    }

    /**
     * Allows to get code required size
     * @return code required size as [Int]
     * */
    fun getCodeRequiredSize(): Int {
        return authenticationSource.getCodeRequiredSize()
    }

    /**
     * Allows to get user authentication state
     * @return user authentication state as [Boolean]
     * */
    fun isUserAuthenticated(): Boolean {
        return authenticationSource.isUserAuthenticated()
    }

    private fun notifyIfCompleted(authResponse: AuthResponse) {
        if (authResponse == AuthResponse.Complete) {
            externalScope.launch(dispatchersProvider.IO) {

                // Notify all listeners about log in
                authenticationListeners.forEach {
                    it.onLogIn()
                }
            }
        }
    }
}