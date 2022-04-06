package com.ferelin.stockprice.shared

import com.ferelin.stockprice.shared.data.di.apiModule
import com.ferelin.stockprice.shared.data.di.repositoryModule
import com.ferelin.stockprice.shared.data.di.storageModule
import com.ferelin.stockprice.shared.domain.di.useCaseModule
import com.ferelin.stockprice.shared.ui.di.viewModelModule
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
