package com.ferelin.stockprice

import com.ferelin.stockprice.data.di.apiModule
import com.ferelin.stockprice.data.di.repositoryModule
import com.ferelin.stockprice.data.di.storageModule
import com.ferelin.stockprice.domain.di.useCaseModule
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
    nativeModule()
  )
}
