package com.ferelin.stockprice.dataInteractor.dataManager.workers

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


/*
* Worker that is responsible for:
*   - Notifications about network state.
* */
class NetworkConnectivityWorker(service: ConnectivityManager, networkRequest: NetworkRequest) {

    private val mIsNetworkAvailableState = MutableStateFlow(true)
    val isNetworkAvailableState: StateFlow<Boolean>
        get() = mIsNetworkAvailableState

    init {
        service.registerNetworkCallback(
            networkRequest,
            object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    mIsNetworkAvailableState.value = true
                }

                override fun onLost(network: Network) {
                    mIsNetworkAvailableState.value = false
                }

                override fun onUnavailable() {
                    mIsNetworkAvailableState.value = false
                }
            })
    }
}