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
import com.ferelin.feature_login.mapper.AuthStateMapper
import com.ferelin.feature_login.viewData.AuthProcessingState
import com.ferelin.navigation.Router
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val mAuthenticationInteractor: AuthenticationInteractor,
    private val mAuthStateMapper: AuthStateMapper,
    private val mRouter: Router,
    private val mDispatchersProvider: DispatchersProvider
) : ViewModel() {

    private val mAuthProcessingState =
        MutableStateFlow<AuthProcessingState>(AuthProcessingState.None())
    val authProcessingState: StateFlow<AuthProcessingState>
        get() = mAuthProcessingState

    private var mLogInJob: Job? = null

    fun tryToLogIn(holder: Activity, phone: String) {
        if (mAuthProcessingState.value is AuthProcessingState.Processing) {
            return
        }

        mLogInJob?.cancel()
        mLogInJob = viewModelScope.launch(mDispatchersProvider.IO) {
            mAuthenticationInteractor.tryToLogIn(holder, phone)
                .map { mAuthStateMapper.map(it) }
                .collect { authState ->
                    mAuthProcessingState.value = authState
                    onAuthStateChanged(authState)
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

    fun onBackClicked() {
        mRouter.back()
    }

    private fun onAuthStateChanged(state: AuthProcessingState) {
        if (state is AuthProcessingState.Complete) {
            mRouter.back()
        }
    }
}