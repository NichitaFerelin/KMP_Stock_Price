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

package com.ferelin.stockprice.ui.login

import android.app.Activity
import androidx.lifecycle.viewModelScope
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginViewModel : BaseViewModel() {

    private val mStateAuthenticationProcess =
        MutableStateFlow<DataNotificator<RepositoryMessages>>(DataNotificator.None())
    val stateAuthenticationProcess: StateFlow<DataNotificator<RepositoryMessages>>
        get() = mStateAuthenticationProcess.asStateFlow()

    private var mInputPhoneNumber = ""
    private var mInputCode = ""

    private var mSignInJob: Job? = null

    override fun initObserversBlock() {
        // Do nothing
    }

    fun onSendCodeClicked(holderActivity: Activity, text: String) {
        viewModelScope.launch(mCoroutineContext.IO) {
            mSignInJob = launch {
                mDataInteractor.tryToSignIn(holderActivity, "+$text").collect { notificator ->
                    mStateAuthenticationProcess.value = notificator
                }
            }
        }
    }

    fun onPhoneNumberChanged(number: String) {
        if (number != mInputPhoneNumber) {
            mInputPhoneNumber = number

            if (mStateAuthenticationProcess.value !is DataNotificator.None) {
                mStateAuthenticationProcess.value = DataNotificator.None()
                mSignInJob?.cancel()
                mSignInJob = null
            }
        }
    }

    fun onCodeChanged(text: String) {
        if (text != mInputCode) {
            mInputCode = text

            if (text.length == 6) {
                mDataInteractor.logInWithCode(text)
            }
        }
    }
}