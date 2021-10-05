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
import com.ferelin.domain.interactors.companies.CompaniesInteractorImpl
import com.ferelin.domain.interactors.searchRequests.SearchRequestsInteractorImpl
import com.ferelin.domain.sources.AuthenticationSource
import com.ferelin.domain.sources.AuthenticationState
import com.ferelin.shared.AuthenticationListener
import com.ferelin.shared.DispatchersProvider
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class AuthenticationInteractor @Inject constructor(
    private val mAuthenticationSource: AuthenticationSource,
    private val mDispatchersProvider: DispatchersProvider,
    private val mAuthenticationListeners: List<@JvmSuppressWildcards AuthenticationListener>,
    @Named("ExternalScope") private val mExternalScope: CoroutineScope
) {
    fun tryToLogIn(holderActivity: Activity, phone: String): Flow<AuthenticationState> {
        return mAuthenticationSource.tryToLogIn(holderActivity, phone)
            .onEach { notifyIfCompleted(it) }
    }

    suspend fun completeAuthentication(code: String) {
        mAuthenticationSource.completeAuthentication(code)
    }

    suspend fun logOut() {
        mExternalScope.launch(mDispatchersProvider.IO) {
            mAuthenticationSource.logOut()

            mAuthenticationListeners.forEach {
                it.onLogOut()
            }
        }
    }

    suspend fun getCodeRequiredSize(): Int {
        return mAuthenticationSource.getCodeRequiredSize()
    }

    suspend fun getUserToken(): String? {
        return mAuthenticationSource.getUserToken()
    }

    suspend fun isUserAuthenticated(): Boolean {
        return mAuthenticationSource.isUserAuthenticated()
    }

    private fun notifyIfCompleted(authenticationState: AuthenticationState) {
        if (authenticationState == AuthenticationState.Complete) {
            mExternalScope.launch(mDispatchersProvider.IO) {
                mAuthenticationListeners.forEach {
                    it.onLogIn()
                }
            }
        }
    }
}