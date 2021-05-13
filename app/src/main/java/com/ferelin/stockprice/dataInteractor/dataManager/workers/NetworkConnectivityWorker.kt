package com.ferelin.stockprice.dataInteractor.dataManager.workers

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

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [NetworkConnectivityWorker] providing a network state: [mStateIsNetworkAvailable]
 */

@Singleton
open class NetworkConnectivityWorker @Inject constructor(
    service: ConnectivityManager, networkRequest: NetworkRequest
) {

    private val mStateIsNetworkAvailable = MutableStateFlow(isNetworkAvailable(service))
    val stateIsNetworkAvailable: StateFlow<Boolean>
        get() = mStateIsNetworkAvailable

    init {
        service.registerNetworkCallback(
            networkRequest,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    mStateIsNetworkAvailable.value = true
                }

                override fun onLost(network: Network) {
                    mStateIsNetworkAvailable.value = false
                }

                override fun onUnavailable() {
                    mStateIsNetworkAvailable.value = false
                }
            })
    }

    private fun isNetworkAvailable(service: ConnectivityManager): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            service.activeNetwork != null
        } else service.activeNetworkInfo != null && service.activeNetworkInfo!!.isConnected
    }
}