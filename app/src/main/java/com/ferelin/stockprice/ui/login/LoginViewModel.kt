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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel : BaseViewModel() {

    private val mStateSignIn = MutableSharedFlow<Unit>()
    val stateSignIn: SharedFlow<Unit>
        get() = mStateSignIn

    private val mStateCodeSent = MutableStateFlow(false)
    val stateCodeSent: StateFlow<Boolean>
        get() = mStateCodeSent

    private val mStateIsLoading = MutableStateFlow(false)
    val stateIsLoading: StateFlow<Boolean>
        get() = mStateIsLoading

    private var mInputPhoneNumber = ""
    private var mInputCode = ""

    val eventError: Flow<String>
        get() = mDataInteractor.sharedAuthenticationError
            .onEach { mStateIsLoading.value = false }


    override fun initObserversBlock() {
        // Do nothing
    }

    fun onSendCodeClicked(holderActivity: Activity, text: String) {
        viewModelScope.launch(mCoroutineContext.IO) {
            mStateIsLoading.value = true
            mDataInteractor.tryToSignIn(holderActivity, "+$text").collect { message ->
                when (message) {
                    is RepositoryMessages.Ok -> mStateSignIn.emit(Unit)
                    is RepositoryMessages.CodeSent -> mStateCodeSent.value = true
                    else -> Unit
                }
                mStateIsLoading.value = false
            }
        }
    }

    fun onPhoneNumberChanged(number: String) {
        if (number != mInputPhoneNumber) {
            mInputPhoneNumber = number
            mStateCodeSent.value = false
        }
    }

    fun onCodeChanged(text: String) {
        if (text != mInputCode) {
            mInputCode = text

            if (text.length == 6) {
                mStateIsLoading.value = true
                mDataInteractor.logInWithCode(text)
            }
        }
    }
}