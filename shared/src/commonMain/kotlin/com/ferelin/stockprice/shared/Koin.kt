package com.ferelin.stockprice

import com.ferelin.stockprice.androidApp.data.di.apiModule
import com.ferelin.stockprice.androidApp.data.di.repositoryModule
import com.ferelin.stockprice.androidApp.data.di.storageModule
import com.ferelin.stockprice.androidApp.domain.di.useCaseModule
import com.ferelin.stockprice.androidApp.ui.di.viewModelModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(
  appDeclaration: KoinAppDeclaration = {}
) = startKoin {
  allowOverride(override = false)
  appDeclaration()
  modules(
    apiModule,
    repositoryModule,
    storageModule,
    useCaseModule,
    viewModelModule,
    nativeModule()
  )
}
