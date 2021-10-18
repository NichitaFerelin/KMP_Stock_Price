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
import kotlinx.coroutines.Dispatchers
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
    private val authenticationInteractor: AuthenticationInteractor,
    private val companiesInteractor: CompaniesInteractor,
    private val searchRequestsInteractor: SearchRequestsInteractor,
    private val networkResolver: NetworkResolver,
    private val router: Router,
    private val menuOptionsProvider: MenuOptionsProvider
) : ViewModel() {

    private val _optionsLoadState = MutableStateFlow<LoadState<Unit>>(LoadState.None())
    val optionsLoadState: StateFlow<LoadState<Unit>> = _optionsLoadState.asStateFlow()

    private val _messageEvent = MutableSharedFlow<Event>()
    val messageEvent: SharedFlow<Event> = _messageEvent.asSharedFlow()

    val optionsAdapter: BaseRecyclerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        BaseRecyclerAdapter(
            createOptionsAdapter(this::onOptionClick)
        ).apply { setHasStableIds(true) }
    }

    fun loadOptions() {
        viewModelScope.launch {
            val isUserAuth = authenticationInteractor.isUserAuthenticated()
            val menuOptions = menuOptionsProvider.buildMenuOptions(isUserAuth)

            withContext(Dispatchers.Main) {
                optionsAdapter.setData(menuOptions)
            }
        }
    }

    fun onBackClick() {
        router.back()
    }

    private fun onOptionClick(optionViewData: OptionViewData) {
        viewModelScope.launch {
            when (optionViewData.type) {
                OptionType.AUTH -> onAuthClick()
                OptionType.CLEAR_DATA -> onClearClick()
            }
        }
    }

    private suspend fun onAuthClick() {
        if (authenticationInteractor.isUserAuthenticated()) {
            authenticationInteractor.logOut()
            val updatedMenuOptions = menuOptionsProvider.buildMenuOptions(false)

            withContext(Dispatchers.Main) {
                optionsAdapter.setData(updatedMenuOptions)
            }
            _messageEvent.emit(Event.LOG_OUT_COMPLETE)
        } else {
            router.fromSettingsToLogin()
        }
    }

    private suspend fun onClearClick() {
        companiesInteractor.eraseUserData()
        searchRequestsInteractor.eraseUserData()

        val event = if (
            networkResolver.isNetworkAvailable
            || !authenticationInteractor.isUserAuthenticated()
        ) {
            Event.DATA_CLEARED
        } else {
            Event.DATA_CLEARED_NO_NETWORK
        }
        _messageEvent.emit(event)
    }
}