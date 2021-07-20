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

package com.ferelin.stockprice.dataInteractor.workers.authentication

import android.app.Activity
import com.ferelin.repository.Repository
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [AuthenticationWorker] is an entity for interacting with repository authorization methods.
 */
@Singleton
class AuthenticationWorker @Inject constructor(
    private val mRepository: Repository,
    private val mAppScope: CoroutineScope
) : AuthenticationWorkerStates {

    private val mStateIsUserAuthenticated = MutableStateFlow(mRepository.isUserAuthenticated())
    override val stateIsUserAuthenticated: StateFlow<Boolean>
        get() = mStateIsUserAuthenticated.asStateFlow()

    /**
     * Sends a request for an authorization attempt by the entered phone number.
     * [holderActivity] is a host activity by which firebase will execute authorization
     * [phoneNumber] is a phone number by which need to authorize user
     * [onLogIn] is a callback that will be executed when authorization will be completed
     * [onError] is a callback that will be executed when authorization will be failed
     * @return flow with authorization messages
     * */
    suspend fun tryToSignIn(
        holderActivity: Activity,
        phoneNumber: String,
        onLogIn: suspend (Boolean) -> Unit,
        onError: suspend (RepositoryMessages) -> Unit
    ): Flow<DataNotificator<RepositoryMessages>> = callbackFlow {
        var taskNotificator: DataNotificator<RepositoryMessages> = DataNotificator.Loading()
        trySend(taskNotificator)

        mRepository.tryToSignIn(holderActivity, phoneNumber).collect { response ->
            when (response) {
                is RepositoryResponse.Success -> {
                    taskNotificator = DataNotificator.DataPrepared(response.data)
                    trySend(taskNotificator)

                    if (response.data is RepositoryMessages.Ok) {
                        mStateIsUserAuthenticated.value = true
                        onLogIn.invoke(mRepository.isUserAuthenticated())
                    }
                }
                is RepositoryResponse.Failed -> {
                    taskNotificator = DataNotificator.Failed()
                    trySend(taskNotificator)

                    onError.invoke(response.message)
                }
            }
        }

        awaitClose()
    }

    /**
     * After an authorization attempt, a code is sent to the phone,
     * which must be entered and sent to the server
     * */
    fun logInWithCode(code: String) {
        mRepository.logInWithCode(code)
    }

    fun logOut() {
        mStateIsUserAuthenticated.value = false
        mAppScope.launch { mRepository.logOut() }
    }
}