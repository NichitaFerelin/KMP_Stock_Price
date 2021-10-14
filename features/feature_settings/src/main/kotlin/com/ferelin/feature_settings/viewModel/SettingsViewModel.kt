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

package com.ferelin.feature_settings.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.adapter.base.BaseRecyclerAdapter
import com.ferelin.core.adapter.options.createOptionsAdapter
import com.ferelin.core.utils.LoadState
import com.ferelin.core.utils.MenuOptionsProvider
import com.ferelin.core.utils.OptionType
import com.ferelin.core.viewData.OptionViewData
import com.ferelin.domain.interactors.AuthenticationInteractor
import com.ferelin.navigation.Router
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val mAuthenticationInteractor: AuthenticationInteractor,
    private val mRouter: Router,
    private val mMenuOptionsProvider: MenuOptionsProvider,
    private val mDispatchersProvider: DispatchersProvider
) : ViewModel() {

    private val mOptionsLoadState = MutableStateFlow<LoadState<Unit>>(LoadState.None())
    val optionsLoadState: StateFlow<LoadState<Unit>>
        get() = mOptionsLoadState.asStateFlow()

    val optionsAdapter: BaseRecyclerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        BaseRecyclerAdapter(
            createOptionsAdapter(this::onOptionClick)
        ).apply { setHasStableIds(true) }
    }

    fun loadOptions() {
        viewModelScope.launch(mDispatchersProvider.IO) {
            val isUserAuth = mAuthenticationInteractor.isUserAuthenticated()
            val menuOptions = mMenuOptionsProvider.buildMenuOptions(isUserAuth)

            withContext(mDispatchersProvider.Main) {
                optionsAdapter.setData(menuOptions)
            }
        }
    }

    fun onBackClick() {
        mRouter.back()
    }

    private fun onOptionClick(optionViewData: OptionViewData) {
        when (optionViewData.type) {
            OptionType.AUTH -> {
            }
            OptionType.CLEAR_DATA -> {
            }
        }
    }
}