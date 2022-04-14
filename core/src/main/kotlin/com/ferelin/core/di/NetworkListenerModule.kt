package com.ferelin.core.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.ferelin.core.network.NetworkListener
import com.ferelin.core.network.NetworkListenerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val networkListenerModule = module {
    factory { buildNetworkRequest() }

    factory {
        androidContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    single<NetworkListener> { NetworkListenerImpl(get(), get()) }
}

internal fun buildNetworkRequest(): NetworkRequest {
    return NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .build()
}