package com.ferelin.stockprice

import android.app.Application
import com.ferelin.stockprice.di.AppComponent
import com.ferelin.stockprice.di.DaggerAppComponent
import timber.log.Timber
import kotlin.LazyThreadSafetyMode.*

class App : Application() {
  val appComponent: AppComponent by lazy(NONE) {
    DaggerAppComponent.builder()
      .context(context = this)
      .build()
  }

  override fun onCreate() {
    super.onCreate()
    Timber.plant(Timber.DebugTree())
  }
}