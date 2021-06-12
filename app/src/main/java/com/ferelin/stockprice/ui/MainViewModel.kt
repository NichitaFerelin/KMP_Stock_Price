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

    init {
        initObserversBlock()
    }

    private val mEventObserverCompanyChanged = MutableSharedFlow<AdaptiveCompany?>(1)
    val eventObserverCompanyChanged: SharedFlow<AdaptiveCompany?>
        get() = mEventObserverCompanyChanged

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
                    launch { mDataInteractor.openConnection().collect() }
                } else mNetworkWasLost = true
            }
        }

    var isServiceRunning = false

    val eventCriticalError: SharedFlow<String>
        get() = mDataInteractor.sharedPrepareCompaniesError

    val eventApiLimitError: SharedFlow<String>
        get() = mDataInteractor.sharedApiLimitError

    val eventOnFavouriteCompaniesLimitError: SharedFlow<String>
        get() = mDataInteractor.sharedFavouriteCompaniesLimitReached

    private var mObserverCompanyCollectorJob: Job? = null

    private var mNetworkWasLost: Boolean = false

    private var mPrepareDataJob: Job? = null

    override fun initObserversBlock() {
        viewModelScope.launch(Dispatchers.IO) {
            mPrepareDataJob = launch { mDataInteractor.prepareData() }
            launch { collectCompanyUpdatesForObserver() }
        }
    }

    private suspend fun collectCompanyUpdatesForObserver() {
        mDataInteractor.stateCompanyForObserver.collect { onCompanyForObserverChanged(it) }
    }

    private suspend fun onCompanyForObserverChanged(company: AdaptiveCompany?) {
        if (company == null) {
            mObserverCompanyCollectorJob?.cancel()
            mEventObserverCompanyChanged.emit(null)
            return
        }

        mEventObserverCompanyChanged.emit(company)
        collectObserverCompanyUpdates(company)
    }

    private fun collectObserverCompanyUpdates(target: AdaptiveCompany) {
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

    private fun restartWebSocket() {
        mDataInteractor.prepareToWebSocketReconnection()
        mNetworkWasLost = false
    }
}