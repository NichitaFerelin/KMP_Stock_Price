package com.ferelin.core.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.ferelin.core.network.NetworkListener
import com.ferelin.core.network.NetworkListenerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module(includes = [NetworkListenerModuleBinds::class])
class NetworkListenerModule {
  @Provides
  fun networkRequest(): NetworkRequest {
    return buildNetworkRequest()
  }

  @Provides
  fun connectivityManager(context: Context): ConnectivityManager {
    return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  }
}

@Module
internal interface NetworkListenerModuleBinds {
  @Binds
  fun networkListener(networkListenerImpl: NetworkListenerImpl): NetworkListener
}

internal fun buildNetworkRequest(): NetworkRequest {
  return NetworkRequest.Builder()
    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
    .build()
}