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
import com.ferelin.core.resolvers.NetworkResolver
import com.ferelin.domain.interactors.AuthenticationInteractor
import com.ferelin.domain.sources.AuthResponse
import com.ferelin.navigation.Router
import com.ferelin.shared.LoadState
import com.ferelin.shared.NetworkListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val authenticationInteractor: AuthenticationInteractor,
    private val networkResolver: NetworkResolver,
    private val router: Router,
) : ViewModel(), NetworkListener {

    private val _authenticationLoad = MutableStateFlow<LoadState<AuthResponse>>(LoadState.None())
    val authenticationLoad: StateFlow<LoadState<AuthResponse>> = _authenticationLoad.asStateFlow()

    private val _networkState = MutableSharedFlow<Boolean>()
    val networkState: SharedFlow<Boolean> = _networkState.asSharedFlow()

    private var authJob: Job? = null

    val requiredCodeSize = authenticationInteractor.getCodeRequiredSize()

    init {
        networkResolver.registerNetworkListener(this)
    }

    override suspend fun onNetworkAvailable() {
        _networkState.emit(true)
    }

    override suspend fun onNetworkLost() {
        _networkState.emit(false)
    }

    override fun onCleared() {
        networkResolver.unregisterNetworkListener(this)
        super.onCleared()
    }

    fun onSendCodeClick(holderActivity: Activity, phone: String) {
        if (!networkResolver.isNetworkAvailable) {
            return
        }

        _authenticationLoad.value.let { authLoadState ->
            if (authLoadState is LoadState.Prepared
                && authLoadState.data == AuthResponse.Complete
            ) return
        }

        authJob?.cancel()
        authJob = viewModelScope.launch {
            authenticationInteractor
                .tryToLogIn(holderActivity, "+$phone")
                .collect { authResponse ->

                    _authenticationLoad.value = when (authResponse) {
                        AuthResponse.CodeSent -> LoadState.Prepared(authResponse)
                        AuthResponse.CodeProcessing -> LoadState.Loading(authResponse)
                        AuthResponse.PhoneProcessing -> LoadState.Loading(authResponse)
                        AuthResponse.TooManyRequests -> LoadState.Error(authResponse)
                        AuthResponse.EmptyPhone -> LoadState.Error(authResponse)
                        AuthResponse.Error -> LoadState.Error(authResponse)
                        AuthResponse.Complete -> {
                            router.back()
                            LoadState.Prepared(authResponse)
                        }
                    }
                }
        }
    }

    fun onCodeChanged(code: String) {
        if (!networkResolver.isNetworkAvailable) {
            return
        }

        viewModelScope.launch {
            if (code.length == authenticationInteractor.getCodeRequiredSize()) {
                authenticationInteractor.completeAuthentication(code)
            }
        }
    }

    fun onBackClick() {
        router.back()
    }
}