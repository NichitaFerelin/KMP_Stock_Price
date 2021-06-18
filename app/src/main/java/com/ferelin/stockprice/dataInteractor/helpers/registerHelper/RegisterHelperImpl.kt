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

package com.ferelin.stockprice.dataInteractor.helpers.registerHelper

import com.ferelin.repository.Repository
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.dataManager.DataMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisterHelperImpl @Inject constructor(
    private val mRepository: Repository,
    private val mDataMediator: DataMediator
) : RegisterHelper {

    override suspend fun tryToRegister(login: String): Flow<Boolean> {
        return mRepository.tryToRegister(mRepository.getUserAuthenticationId()!!, login)
            .onEach { response ->
                if (response is RepositoryResponse.Failed) {
                    mDataMediator.onRegisterError(response.message)
                }
            }
            .filter { it is RepositoryResponse.Success }
            .map { (it as RepositoryResponse.Success).data }
    }

    override suspend fun isUserRegistered(): Boolean {
        /**
         * TODO
         * Compares local and remote registration state.
         *
        val localState = mRepositoryHelper.getUserRegisterState() == true

        if (!localState) {
        val remoteState = mRepositoryHelper.isUserIdExist(mRepositoryHelper.provideUserId()!!)

        if (!remoteState) {
        return false
        } else mRepositoryHelper.setRegisterStateToPreferences(true)
        }

        return true*/
        return false
    }

    override suspend fun findUser(login: String): Boolean {
        return mRepository.isUserExist(login)
    }
}