package com.ferelin.core.network

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface NetworkListener {
  val networkState: Flow<Boolean>
}

@SuppressLint("MissingPermission")
internal class NetworkListenerImpl @Inject constructor(
  service: ConnectivityManager,
  networkRequest: NetworkRequest
) : NetworkListener {
  private val _networkState = MutableStateFlow(
    value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      service.activeNetwork != null
    } else {
      service.activeNetworkInfo != null && service.activeNetworkInfo!!.isConnected
    }
  )
  override val networkState: Flow<Boolean> = _networkState.asStateFlow()

  init {
    service.registerNetworkCallback(
      networkRequest,
      object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
          _networkState.value = true
        }

        override fun onLost(network: Network) {
          _networkState.value = false
        }

        override fun onUnavailable() {
          _networkState.value = false
        }
      })
  }

  companion object {
    // TODO вынести где-то рядом с di
    fun buildNetworkRequest(): NetworkRequest {
      return NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .build()
    }
  }
}