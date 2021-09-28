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
import com.ferelin.domain.sources.AuthenticationState
import com.ferelin.feature_login.mapper.AuthenticationMapper
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthenticationLoadState {
    object Authenticated : AuthenticationLoadState()
    class Loading(val state: AuthenticationState? = null) : AuthenticationLoadState()
    class Error(val error: AuthenticationState) : AuthenticationLoadState()
    object None : AuthenticationLoadState()
}

class LoginViewModel @Inject constructor(
    private val mAuthenticationInteractor: AuthenticationInteractor,
    private val mAuthenticationMapper: AuthenticationMapper,
    private val mDispatchersProvider: DispatchersProvider
) : ViewModel() {

    private val mAuthenticationState =
        MutableStateFlow<AuthenticationLoadState>(AuthenticationLoadState.None)
    val authenticationState: StateFlow<AuthenticationLoadState>
        get() = mAuthenticationState

    fun tryToLogIn(holder: Activity, phone: String) {
        viewModelScope.launch(mDispatchersProvider.IO) {
            mAuthenticationState.value = AuthenticationLoadState.Loading()

            mAuthenticationState.value = mAuthenticationInteractor
                .tryToLogIn(holder, phone)
                .map(mAuthenticationMapper::map)
                .stateIn(viewModelScope)
                .value
        }
    }

    fun onCodeChanged(code: String) {
        if (code.length == 6) {
            viewModelScope.launch(mDispatchersProvider.IO) {
                mAuthenticationInteractor.completeAuthentication(code)
            }
        }
    }
}