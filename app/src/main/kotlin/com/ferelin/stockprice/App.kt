package com.ferelin.stockprice

import android.app.Application
import android.os.StrictMode
import com.ferelin.core.data.di.networkModule
import com.ferelin.core.data.di.repositoryModule
import com.ferelin.core.data.di.storageModule
import com.ferelin.core.di.coroutineModule
import com.ferelin.core.di.networkListenerModule
import com.ferelin.core.domain.di.useCaseModule
import com.ferelin.features.home.home.homeModule
import com.ferelin.features.stocks.stocks.stocksModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin()

        if (BuildConfig.DEBUG) attachStrictMode()
    }

    private fun startKoin() {
        startKoin {
            allowOverride(override = false)
            androidLogger(level = if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@App)
            modules(koinModules)
        }
    }

    private fun attachStrictMode() {
        val threadPolicy = StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .build()
        StrictMode.setThreadPolicy(threadPolicy)

        val vmPolicy = StrictMode.VmPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .build()
        StrictMode.setVmPolicy(vmPolicy)
    }
}

private val koinModules = listOf(
    networkModule,
    repositoryModule,
    storageModule,
    useCaseModule,
    coroutineModule,
    networkListenerModule,
    homeModule,
    stocksModule
)