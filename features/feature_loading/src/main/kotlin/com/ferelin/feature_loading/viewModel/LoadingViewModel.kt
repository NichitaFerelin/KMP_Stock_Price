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

package com.ferelin.feature_loading.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.domain.interactors.FirstLaunchInteractor
import com.ferelin.navigation.Router
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoadingViewModel @Inject constructor(
    private val firstLaunchInteractor: FirstLaunchInteractor,
    private val router: Router
) : ViewModel() {

    private val _loadPreparedState = MutableStateFlow(false)
    val loadPreparedState: StateFlow<Boolean> = _loadPreparedState.asStateFlow()

    private var isFirstTimeLaunch = false

    init {
        viewModelScope.launch {
            prepareLaunch()
        }
    }

    fun onAnimationsStopped() {
        viewModelScope.launch {
            if (isFirstTimeLaunch) {
                router.fromLoadingToStocksPager()
                firstLaunchInteractor.cache(false)
            } else {
                router.fromLoadingToStocksPager()
            }
        }
    }

    private suspend fun prepareLaunch() {
        isFirstTimeLaunch = firstLaunchInteractor.get()
        _loadPreparedState.value = true
    }
}