package com.ferelin.stockprice

import android.app.Application
import android.os.StrictMode
import com.ferelin.core.di.coroutineModule
import com.ferelin.core.di.networkListenerModule
import com.ferelin.core.di.permissionModule
import com.ferelin.core.di.storagePathBuilderModule
import com.ferelin.features.about.about.aboutModule
import com.ferelin.features.about.chart.chartModule
import com.ferelin.features.about.news.newsModule
import com.ferelin.features.about.profile.profileModule
import com.ferelin.features.home.cryptos.cryptosModule
import com.ferelin.features.home.favourite.favouriteStocksModule
import com.ferelin.features.home.home.homeModule
import com.ferelin.features.home.stocks.stocksModule
import com.ferelin.features.login.loginModule
import com.ferelin.features.search.searchModule
import com.ferelin.features.settings.settingsModule
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
  coroutineModule,
  networkListenerModule,
  permissionModule,
  storagePathBuilderModule,
  aboutModule,
  chartModule,
  newsModule,
  profileModule,
  cryptosModule,
  favouriteStocksModule,
  homeModule,
  stocksModule,
  loginModule,
  searchModule,
  settingsModule
)