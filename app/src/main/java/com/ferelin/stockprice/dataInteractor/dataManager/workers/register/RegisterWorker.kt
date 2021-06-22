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

package com.ferelin.stockprice.dataInteractor.dataManager.workers.register

import com.ferelin.repository.Repository
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisterWorker @Inject constructor(
    private val mRepository: Repository
) : RegisterWorkerStates {

    private val mStateUserRegister = MutableStateFlow<Boolean?>(null)
    override val stateUserRegister: StateFlow<Boolean?>
        get() = mStateUserRegister

    private var mUserLogin: String? = null
    override val userLogin: String?
        get() = mUserLogin

    suspend fun prepareUserRegisterState() {
        if (!mRepository.isUserAuthenticated()) {
            mStateUserRegister.value = false
            return
        }

        val localIsRegistered = mRepository.getUserRegisterState() == true
        if (!localIsRegistered) {
            val remoteUserResponse =
                mRepository.findUserById(mRepository.getUserAuthenticationId()!!)

            when (remoteUserResponse) {
                is RepositoryResponse.Success -> {
                    mUserLogin = remoteUserResponse.data!!
                    mStateUserRegister.value = true
                    mRepository.setUserRegisterState(true)
                    mRepository.setUserLogin(mUserLogin!!)
                }
                is RepositoryResponse.Failed -> mStateUserRegister.value = false
            }
        } else mStateUserRegister.value = true
    }

    fun onLogOut() {
        mUserLogin = ""
        mStateUserRegister.value = null
    }

    suspend fun onLogIn() {
        prepareUserRegisterState()
    }

    suspend fun tryToRegister(
        login: String,
        onError: suspend (RepositoryMessages) -> Unit
    ): Flow<Boolean> {
        return mRepository.tryToRegister(mRepository.getUserAuthenticationId()!!, login)
            .onEach { response ->
                if (response is RepositoryResponse.Failed) {
                    onError.invoke(response.message)
                }
            }
            .filter { it is RepositoryResponse.Success }
            .onEach {
                mStateUserRegister.value = true
                mUserLogin = login
                mRepository.setUserRegisterState(true)
            }
            .map { (it as RepositoryResponse.Success).data }
    }
}