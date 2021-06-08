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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel(
    private val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider(),
    private val mDataInteractor: DataInteractor,
    mApplication: Application
) : AndroidViewModel(mApplication) {

    init {
        initObservers()
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

    private var mObserverCompanyCollectorJob: Job? = null

    private var mNetworkWasLost: Boolean = false

    private var mPrepareDataJob: Job? = null

    fun initObservers() {
        viewModelScope.launch(mCoroutineContext.IO) {
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
        mObserverCompanyCollectorJob = viewModelScope.launch(mCoroutineContext.IO) {
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