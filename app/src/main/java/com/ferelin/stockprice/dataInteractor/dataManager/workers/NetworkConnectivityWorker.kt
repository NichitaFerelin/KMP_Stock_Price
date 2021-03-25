package com.ferelin.stockprice.dataInteractor.dataManager.workers

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NetworkConnectivityWorker(service: ConnectivityManager, networkRequest: NetworkRequest) {

    private val mIsNetworkAvailableState = MutableStateFlow(true)
    val isNetworkAvailableState: StateFlow<Boolean>
        get() = mIsNetworkAvailableState

    val networkCallback = service.registerNetworkCallback(
        networkRequest,
        object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                mIsNetworkAvailableState.value = true
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                mIsNetworkAvailableState.value = false
            }

            override fun onUnavailable() {
                super.onUnavailable()
                mIsNetworkAvailableState.value = false
            }
        })
}