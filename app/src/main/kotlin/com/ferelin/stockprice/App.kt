package com.ferelin.stockprice

import android.app.Application
import android.os.StrictMode
import com.ferelin.stockprice.di.AppComponent
import com.ferelin.stockprice.di.DaggerAppComponent
import timber.log.Timber
import kotlin.LazyThreadSafetyMode.NONE

class App : Application() {

  val appComponent: AppComponent by lazy(NONE) {
    DaggerAppComponent.builder()
      .context(context = this)
      .build()
  }

  override fun onCreate() {
    super.onCreate()
    Timber.plant(Timber.DebugTree())

    if (BuildConfig.DEBUG) {
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
}