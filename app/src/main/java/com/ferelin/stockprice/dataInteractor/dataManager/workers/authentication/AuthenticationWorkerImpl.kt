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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationWorkerImpl @Inject constructor(
    private val mRepository: Repository,
    private val mSynchronizationManager: SynchronizationManager
) : AuthenticationWorker, AuthenticationWorkerStates {

    private var mUserLogin: String? = null
    override val userLogin: String?
        get() = mUserLogin

    override fun onUserLoginChanged(newLogin: String) {
        mUserLogin = newLogin
    }

    override suspend fun signIn(
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
                            // Menu items worker on log state changed todo
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

    override fun isUserLogged(): Boolean {
        return mRepository.isUserAuthenticated()
    }

    override fun logInWithCode(code: String) {
        mRepository.logInWithCode(code)
    }

    override suspend fun logOut() {
        mRepository.setUserRegisterState(false)
        mRepository.logOut()
        mSynchronizationManager.onLogOut()
        // TODO mDataMediator.onLogStateChanged(mRepository.isUserAuthenticated())
    }
}