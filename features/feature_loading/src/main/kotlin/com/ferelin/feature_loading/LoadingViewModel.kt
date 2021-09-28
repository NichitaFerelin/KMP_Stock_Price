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

package com.ferelin.feature_loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.domain.interactors.FirstLaunchInteractor
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FirstTimeLoadState {
    class Loaded(val value: Boolean) : FirstTimeLoadState()
    object Loading : FirstTimeLoadState()
    object None : FirstTimeLoadState()
}

class LoadingViewModel @Inject constructor(
    private val mFirstLaunchInteractor: FirstLaunchInteractor,
    private val mDispatchersProvider: DispatchersProvider
) : ViewModel() {

    private val mFirstTimeLaunch = MutableStateFlow<FirstTimeLoadState>(FirstTimeLoadState.None)
    val firstTimeLaunch: StateFlow<FirstTimeLoadState>
        get() = mFirstTimeLaunch

    fun loadFirstTimeLaunch() {
        viewModelScope.launch(mDispatchersProvider.IO) {
            mFirstTimeLaunch.value = FirstTimeLoadState.Loading

            val dbFirstTimeLaunch = mFirstLaunchInteractor.getFirstTimeLaunch()
            mFirstTimeLaunch.value = FirstTimeLoadState.Loaded(dbFirstTimeLaunch)
        }
    }

    fun launched() {
        viewModelScope.launch(mDispatchersProvider.IO) {
            mFirstTimeLaunch.value = FirstTimeLoadState.Loaded(true)
            mFirstLaunchInteractor.cacheFirstTimeLaunch(true)
        }
    }
}