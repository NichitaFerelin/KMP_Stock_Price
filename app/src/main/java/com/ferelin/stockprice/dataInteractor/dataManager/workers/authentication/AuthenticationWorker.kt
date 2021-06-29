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

package com.ferelin.stockprice.dataInteractor.dataManager.workers.authentication

import android.app.Activity
import com.ferelin.repository.Repository
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.syncManager.SynchronizationManager
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationWorker @Inject constructor(
    private val mRepository: Repository,
    private val mSynchronizationManager: SynchronizationManager
) : AuthenticationWorkerStates {

    private val mStateUserLogged = MutableStateFlow<Boolean?>(null)
    override val stateUserLogged: StateFlow<Boolean?>
        get() = mStateUserLogged

    fun prepareAuthenticationState() {
        mStateUserLogged.value = mRepository.isUserAuthenticated()
    }

    suspend fun signIn(
        holderActivity: Activity,
        phone: String,
        onLogStateChanged: suspend (Boolean) -> Unit,
        onError: suspend (RepositoryMessages) -> Unit
    ): Flow<RepositoryMessages> {
        return mRepository.tryToSignIn(holderActivity, phone)
            .onEach { response ->
                when (response) {
                    is RepositoryResponse.Success -> {
                        if (response.data is RepositoryMessages.Ok) {
                            mStateUserLogged.value = true
                            onLogStateChanged.invoke(mRepository.isUserAuthenticated())
                            mSynchronizationManager.onLogIn()
                        }
                    }
                    is RepositoryResponse.Failed -> onError.invoke(response.message)
                }
            }
            .filter { it is RepositoryResponse.Success }
            .map { (it as RepositoryResponse.Success).data }
    }

    fun logInWithCode(code: String) {
        mRepository.logInWithCode(code)
    }

    suspend fun logOut() {
        mRepository.setUserRegisterState(false)
        mStateUserLogged.value = false
        mRepository.logOut()
        mSynchronizationManager.onLogOut()
    }
}