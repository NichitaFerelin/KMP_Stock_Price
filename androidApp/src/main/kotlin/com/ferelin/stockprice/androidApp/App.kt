package com.ferelin.stockprice.androidApp

import android.app.Application
import android.os.StrictMode
import com.ferelin.stockprice.androidApp.ui.viewModelWrapperModule
import com.ferelin.stockprice.shared.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
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
        initKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@App)
            modules(viewModelWrapperModule)
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