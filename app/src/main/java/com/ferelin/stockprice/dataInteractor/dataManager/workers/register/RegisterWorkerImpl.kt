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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisterWorkerImpl @Inject constructor(
    private val mRepository: Repository
) : RegisterWorker {

    override suspend fun tryToRegister(
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
            .map { (it as RepositoryResponse.Success).data }
    }

    override suspend fun isUserRegistered(): Boolean {
        if (!mRepository.isUserAuthenticated()) {
            return false
        }

        val localIsRegistered = mRepository.getUserRegisterState() == true

        if (!localIsRegistered) {
            val remoteIsRegistered =
                mRepository.isUserIdExist(mRepository.getUserAuthenticationId()!!)

            if (!remoteIsRegistered) {
                return false
            } else mRepository.setUserRegisterState(true)
        }

        return true
    }

    override suspend fun findUser(login: String): Boolean {
        return mRepository.isUserExist(login)
    }
}