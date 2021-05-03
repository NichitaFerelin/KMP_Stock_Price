package com.ferelin.stockprice.dataInteractor.dataManager.workers

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


/**
 * [NetworkConnectivityWorker] providing a network state: [mStateIsNetworkAvailable]
 */
class NetworkConnectivityWorker(service: ConnectivityManager, networkRequest: NetworkRequest) {

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