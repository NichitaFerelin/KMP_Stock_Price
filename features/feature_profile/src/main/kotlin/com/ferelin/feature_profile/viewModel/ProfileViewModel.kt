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

package com.ferelin.feature_profile.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.domain.entities.Profile
import com.ferelin.domain.interactors.ProfileInteractor
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileLoadState {
    class Loaded(val profile: Profile) : ProfileLoadState()
    object Loading : ProfileLoadState()
    object None : ProfileLoadState()
}

class ProfileViewModel @Inject constructor(
    private val mProfileInteractor: ProfileInteractor,
    private val mDispatchersProvider: DispatchersProvider
) : ViewModel() {

    private val mProfileLoadState = MutableStateFlow<ProfileLoadState>(ProfileLoadState.None)
    val profileLoadState: StateFlow<ProfileLoadState>
        get() = mProfileLoadState.asStateFlow()

    fun loadProfile(companyId: Int) {
        viewModelScope.launch(mDispatchersProvider.IO) {
            mProfileLoadState.value = ProfileLoadState.Loading

            val response = mProfileInteractor.getProfile(companyId)
            mProfileLoadState.value = ProfileLoadState.Loaded(response)
        }
    }
}