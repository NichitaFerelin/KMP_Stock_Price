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

package com.ferelin.stockprice.ui.bottomDrawerSection.register

import androidx.lifecycle.viewModelScope
import com.ferelin.stockprice.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RegisterViewModel : BaseViewModel() {

    private val mStateRegistered = MutableStateFlow(false)
    val stateRegistered: StateFlow<Boolean>
        get() = mStateRegistered

    val eventRegisterError: SharedFlow<String>
        get() = mDataInteractor.sharedRegisterError

    override fun initObserversBlock() {
        // Do nothing
    }

    private var mRegisterJob: Job? = null

    fun onIconCheckClicked(login: String) {
        tryToRegister(login)
    }

    private fun tryToRegister(login: String) {
        mRegisterJob?.cancel()
        mRegisterJob = viewModelScope.launch(mCoroutineContext.IO) {
            mDataInteractor.tryToRegister(login).collect { success ->
                if (success) mStateRegistered.value = true
            }
        }
    }
}