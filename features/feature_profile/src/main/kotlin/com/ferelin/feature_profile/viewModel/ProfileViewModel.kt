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
import com.ferelin.core.params.ProfileParams
import com.ferelin.domain.useCases.GetProfileUseCase
import com.ferelin.feature_profile.mapper.ProfileMapper
import com.ferelin.feature_profile.viewData.ProfileViewData
import com.ferelin.navigation.Router
import com.ferelin.shared.LoadState
import com.ferelin.shared.ifPrepared
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val profileMapper: ProfileMapper,
    private val router: Router
) : ViewModel() {

    private val _profileLoadState = MutableStateFlow<LoadState<ProfileViewData>>(LoadState.None())
    val profileLoadState: StateFlow<LoadState<ProfileViewData>> = _profileLoadState.asStateFlow()

    var profileParams = ProfileParams()

    init {
        loadProfile()
    }

    fun onPhoneClick(phone: String): Boolean {
        return router.openContacts(phone)
    }

    fun onUrlClick(): Boolean {
        return _profileLoadState.value.ifPrepared { preparedState ->
            router.openUrl(preparedState.data.webUrl)
        } ?: false
    }

    fun onShareClick(
        nameHint: String,
        websiteHint: String,
        countryHint: String,
        industryHint: String,
        phoneHint: String,
        capitalizationHint: String
    ) {
        viewModelScope.launch {
            _profileLoadState.value.ifPrepared { preparedState ->
                val resultText = nameHint + ": " + profileParams.companyName + "\n" +
                        websiteHint + ": " + preparedState.data.webUrl + "\n" +
                        countryHint + " " + preparedState.data.country + "\n" +
                        industryHint + " " + preparedState.data.industry + "\n" +
                        phoneHint + " " + preparedState.data.phone + "\n" +
                        capitalizationHint + " " + preparedState.data.capitalization

                router.shareText(resultText)
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _profileLoadState.value = LoadState.Loading()

            val responseProfile = getProfileUseCase.getBy(profileParams.companyId)
            val mapped = profileMapper.map(responseProfile)
            _profileLoadState.value = LoadState.Prepared(mapped)
        }
    }
}