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
import com.ferelin.core.resolvers.NetworkResolver
import com.ferelin.core.utils.MenuOptionsProvider
import com.ferelin.core.utils.OptionType
import com.ferelin.core.viewData.OptionViewData
import com.ferelin.domain.interactors.AuthenticationInteractor
import com.ferelin.domain.interactors.companies.CompaniesInteractor
import com.ferelin.domain.interactors.searchRequests.SearchRequestsInteractor
import com.ferelin.navigation.Router
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.LoadState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

enum class Event {
    LOG_OUT_COMPLETE,
    DATA_CLEARED,
    DATA_CLEARED_NO_NETWORK
}

class SettingsViewModel @Inject constructor(
    private val mAuthenticationInteractor: AuthenticationInteractor,
    private val mCompaniesInteractor: CompaniesInteractor,
    private val mSearchRequestsInteractor: SearchRequestsInteractor,
    private val mNetworkResolver: NetworkResolver,
    private val mRouter: Router,
    private val mMenuOptionsProvider: MenuOptionsProvider,
    private val mDispatchersProvider: DispatchersProvider
) : ViewModel() {

    private val mOptionsLoadState = MutableStateFlow<LoadState<Unit>>(LoadState.None())
    val optionsLoadState: StateFlow<LoadState<Unit>>
        get() = mOptionsLoadState.asStateFlow()

    private val mMessageEvent = MutableSharedFlow<Event>()
    val messageEvent: SharedFlow<Event>
        get() = mMessageEvent.asSharedFlow()

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
        viewModelScope.launch(mDispatchersProvider.IO) {
            when (optionViewData.type) {
                OptionType.AUTH -> onAuthClick()
                OptionType.CLEAR_DATA -> onClearClick()
            }
        }
    }

    private suspend fun onAuthClick() {
        if (mAuthenticationInteractor.isUserAuthenticated()) {
            mAuthenticationInteractor.logOut()
            val updatedMenuOptions = mMenuOptionsProvider.buildMenuOptions(false)

            withContext(mDispatchersProvider.Main) {
                optionsAdapter.setData(updatedMenuOptions)
            }
            mMessageEvent.emit(Event.LOG_OUT_COMPLETE)
        } else {
            mRouter.fromSettingsToLogin()
        }
    }

    private suspend fun onClearClick() {
        mCompaniesInteractor.clearUserData()
        mSearchRequestsInteractor.clearUserData()

        val event = if (
            mNetworkResolver.isNetworkAvailable
            || !mAuthenticationInteractor.isUserAuthenticated()
        ) {
            Event.DATA_CLEARED
        } else {
            Event.DATA_CLEARED_NO_NETWORK
        }
        mMessageEvent.emit(event)
    }
}