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

package com.ferelin.core.resolvers

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.NetworkListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@SuppressLint("MissingPermission")
@Singleton
class NetworkResolver @Inject constructor(
    private val networkDeps: ArrayList<@JvmSuppressWildcards NetworkListener>,
    private val dispatchersProvider: DispatchersProvider,
    @Named("ExternalScope") private val externalScope: CoroutineScope,
    service: ConnectivityManager,
    networkRequest: NetworkRequest
) {
    private var mIsNetworkAvailable: Boolean
    val isNetworkAvailable: Boolean
        get() = mIsNetworkAvailable

    init {
        mIsNetworkAvailable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            service.activeNetwork != null
        } else {
            service.activeNetworkInfo != null && service.activeNetworkInfo!!.isConnected
        }

        service.registerNetworkCallback(
            networkRequest,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    externalScope.launch(dispatchersProvider.IO) {
                        Timber.d("on network available")
                        mIsNetworkAvailable = true
                        networkDeps.forEach { it.onNetworkAvailable() }
                    }
                }

                override fun onLost(network: Network) {
                    externalScope.launch(dispatchersProvider.IO) {
                        Timber.d("on network lost")
                        mIsNetworkAvailable = false
                        networkDeps.forEach { it.onNetworkLost() }
                    }
                }

                override fun onUnavailable() {
                    externalScope.launch(dispatchersProvider.IO) {
                        Timber.d("on network unavailable")
                        mIsNetworkAvailable = false
                        networkDeps.forEach { it.onNetworkLost() }
                    }
                }
            })
    }

    fun registerNetworkListener(networkListener: NetworkListener) {
        networkDeps.add(networkListener)
    }

    fun unregisterNetworkListener(networkListener: NetworkListener) {
        networkDeps.remove(networkListener)
    }
}