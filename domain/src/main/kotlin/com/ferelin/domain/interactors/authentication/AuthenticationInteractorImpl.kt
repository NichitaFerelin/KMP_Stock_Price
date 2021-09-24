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

package com.ferelin.domain.interactors.authentication

import android.app.Activity
import com.ferelin.domain.internals.AuthenticationInternal
import com.ferelin.domain.sources.AuthenticationSource
import com.ferelin.domain.sources.AuthenticationState
import com.ferelin.shared.AuthenticationListener
import com.ferelin.shared.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AuthenticationInteractorImpl @Inject constructor(
    private val mAuthenticationSource: AuthenticationSource,
    private val mAuthenticationListeners: List<AuthenticationListener>,
    private val mCoroutineContextProvider: CoroutineContextProvider,
    @Named("ExternalScope") private val mExternalScope: CoroutineScope
) : AuthenticationInteractor, AuthenticationInternal {

    override fun tryToLogIn(holderActivity: Activity, phone: String): Flow<AuthenticationState> {
        return mAuthenticationSource.tryToLogIn(holderActivity, phone)
            .onEach { notifyIfCompleted(it) }
    }

    override suspend fun isUserAuthenticated(): Boolean {
        return mAuthenticationSource.isUserAuthenticated()
    }

    override suspend fun completeAuthentication(code: String) {
        mAuthenticationSource.completeAuthentication(code)
    }

    override suspend fun logOut() {
        mExternalScope.launch(mCoroutineContextProvider.IO) {
            mAuthenticationSource.logOut()

            mAuthenticationListeners.forEach {
                it.onLogOut()
            }
        }
    }

    override suspend fun getUserToken(): String? {
        return mAuthenticationSource.getUserToken()
    }

    private fun notifyIfCompleted(authenticationState: AuthenticationState) {
        if (authenticationState == AuthenticationState.Complete) {
            mExternalScope.launch(mCoroutineContextProvider.IO) {
                mAuthenticationListeners.forEach {
                    it.onLogIn()
                }
            }
        }
    }
}