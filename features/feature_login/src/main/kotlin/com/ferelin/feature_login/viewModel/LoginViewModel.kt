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

package com.ferelin.feature_login.viewModel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.domain.interactors.AuthenticationInteractor
import com.ferelin.domain.sources.AuthResponse
import com.ferelin.navigation.Router
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.LoadState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias AuthState = LoadState<AuthResponse>

class LoginViewModel @Inject constructor(
    private val mAuthenticationInteractor: AuthenticationInteractor,
    private val mRouter: Router,
    private val mDispatchersProvider: DispatchersProvider
) : ViewModel() {

    private val mAuthenticationLoadState = MutableStateFlow<AuthState>(LoadState.None())
    val authenticationLoadState: StateFlow<AuthState>
        get() = mAuthenticationLoadState.asStateFlow()

    private var mAuthJob: Job? = null

    fun onSendCodeClick(holderActivity: Activity, phone: String) {
        mAuthenticationLoadState.value.let { authLoadState ->
            if (authLoadState is LoadState.Prepared
                && authLoadState.data == AuthResponse.Complete
            ) return
        }

        mAuthJob?.cancel()
        mAuthJob = viewModelScope.launch(mDispatchersProvider.IO) {
            mAuthenticationInteractor
                .tryToLogIn(holderActivity, "+$phone")
                .collect { authResponse ->

                    mAuthenticationLoadState.value = when (authResponse) {
                        AuthResponse.CodeSent -> LoadState.Prepared(authResponse)
                        AuthResponse.CodeProcessing -> LoadState.Loading(authResponse)
                        AuthResponse.PhoneProcessing -> LoadState.Loading(authResponse)
                        AuthResponse.TooManyRequests -> LoadState.Error(authResponse)
                        AuthResponse.EmptyPhone -> LoadState.Error(authResponse)
                        AuthResponse.Error -> LoadState.Error(authResponse)
                        AuthResponse.Complete -> {
                            mRouter.back()
                            LoadState.Prepared(authResponse)
                        }
                    }
                }
        }
    }

    fun onCodeChanged(code: String) {
        viewModelScope.launch(mDispatchersProvider.IO) {
            if (code.length == mAuthenticationInteractor.getCodeRequiredSize()) {
                mAuthenticationInteractor.completeAuthentication(code)
            }
        }
    }

    fun onBackClick() {
        mRouter.back()
    }
}