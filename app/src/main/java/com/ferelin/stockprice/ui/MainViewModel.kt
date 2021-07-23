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
import com.ferelin.stockprice.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel : BaseViewModel() {

    private var mNetworkWasLost: Boolean = false

    private var mWebSocketJob: Job? = null

    val eventCriticalError: SharedFlow<String>
        get() = mDataInteractor.sharedPrepareCompaniesError

    val eventApiLimitError: SharedFlow<String>
        get() = mDataInteractor.sharedApiLimitError

    val eventOnFavouriteCompaniesLimitError: SharedFlow<String>
        get() = mDataInteractor.sharedFavouriteCompaniesLimitReached

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            launch { mDataInteractor.openWebSocketConnection().collect() }
            launch { collectStateNetwork() }
        }
    }

    private suspend fun collectStateNetwork() {
        mDataInteractor.provideNetworkStateFlow().collect { isAvailable ->
            if (isAvailable) {
                /*
                * When the network is lost -> web socket breaks.
                * */
                if (mNetworkWasLost) {
                    restartWebSocket()
                }
                mWebSocketJob?.cancel()
                mWebSocketJob = viewModelScope.launch(mCoroutineContext.IO) {
                    mDataInteractor.openWebSocketConnection().collect()
                }
            } else mNetworkWasLost = true
        }
    }

    private fun restartWebSocket() {
        mDataInteractor.prepareForWebSocketReconnection()
        mNetworkWasLost = false
    }
}