package com.ferelin.stockprice.ui

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

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.base.BaseViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MainViewModel : BaseViewModel() {

    private var mNetworkWasLost: Boolean = false

    val stateIsNetworkAvailable: Flow<Boolean>
        get() = mDataInteractor.provideNetworkStateFlow().onEach { isAvailable ->
            viewModelScope.launch(mCoroutineContext.IO) {
                if (isAvailable) {
                    /*
                    * When the network is lost -> web socket breaks.
                    * */
                    if (mNetworkWasLost) {
                        restartWebSocket()
                    }
                    launch { mDataInteractor.openWebSocketConnection().collect() }
                } else mNetworkWasLost = true
            }
        }

    val stateIsUserAuthenticated: StateFlow<Boolean>
        get() = mDataInteractor.stateIsUserAuthenticated

    val eventCriticalError: SharedFlow<String>
        get() = mDataInteractor.sharedPrepareCompaniesError

    val eventApiLimitError: SharedFlow<String>
        get() = mDataInteractor.sharedApiLimitError

    val eventOnFavouriteCompaniesLimitError: SharedFlow<String>
        get() = mDataInteractor.sharedFavouriteCompaniesLimitReached

    private val mEventObserverCompanyChanged = MutableSharedFlow<AdaptiveCompany?>(1)
    val eventObserverCompanyChanged: SharedFlow<AdaptiveCompany?>
        get() = mEventObserverCompanyChanged.asSharedFlow()

    private var mObserverCompanyCollectorJob: Job? = null
    var isServiceRunning = false

    var arrowState: Float = 0F
    var isBottomBarFabVisible = false
    var isBottomBarVisible = false

    override fun initObserversBlock() {
        viewModelScope.launch(Dispatchers.IO) {
            collectCompanyUpdatesForObserver()
        }
    }

    private fun collectCompanyUpdatedForService(target: AdaptiveCompany) {
        mObserverCompanyCollectorJob?.cancel()
        mObserverCompanyCollectorJob = viewModelScope.launch(Dispatchers.IO) {
            mDataInteractor.sharedCompaniesUpdates
                .filter { it.data == target }
                .collect {
                    if (!isActive) {
                        cancel()
                    } else mEventObserverCompanyChanged.emit(target)
                }
        }
    }

    private suspend fun collectCompanyUpdatesForObserver() {
        mDataInteractor.stateCompanyForObserver.collect { onCompanyDataForServiceChanged(it) }
    }

    private suspend fun onCompanyDataForServiceChanged(company: AdaptiveCompany?) {
        if (company == null) {
            mObserverCompanyCollectorJob?.cancel()
            mEventObserverCompanyChanged.emit(null)
            return
        }

        mEventObserverCompanyChanged.emit(company)
        collectCompanyUpdatedForService(company)
    }

    private fun restartWebSocket() {
        mDataInteractor.prepareForWebSocketReconnection()
        mNetworkWasLost = false
    }
}