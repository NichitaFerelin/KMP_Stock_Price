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

package com.ferelin.stockprice.resolvers

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.NetworkListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class NetworkResolver @Inject constructor(
    @Named("NetworkDeps") private val mNetworkDeps: List<NetworkListener>,
    @Named("ExternalScope") private val mExternalScope: CoroutineScope,
    private val mDispatchersProvider: DispatchersProvider,
    service: ConnectivityManager,
    networkRequest: NetworkRequest
) {
    init {
        service.registerNetworkCallback(
            networkRequest,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    mExternalScope.launch(mDispatchersProvider.IO) {
                        mNetworkDeps.forEach { it.onNetworkAvailable() }
                    }
                }

                override fun onLost(network: Network) {
                    mExternalScope.launch(mDispatchersProvider.IO) {
                        mNetworkDeps.forEach { it.onNetworkLost() }
                    }
                }

                override fun onUnavailable() {
                    mExternalScope.launch(mDispatchersProvider.IO) {
                        mNetworkDeps.forEach { it.onNetworkLost() }
                    }
                }
            })
    }
}