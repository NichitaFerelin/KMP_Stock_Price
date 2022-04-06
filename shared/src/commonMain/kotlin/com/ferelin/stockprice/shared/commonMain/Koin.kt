package com.ferelin.stockprice.shared.commonMain

import com.ferelin.stockprice.shared.commonMain.data.di.apiModule
import com.ferelin.stockprice.shared.commonMain.data.di.repositoryModule
import com.ferelin.stockprice.shared.commonMain.data.di.storageModule
import com.ferelin.stockprice.shared.commonMain.domain.di.useCaseModule
import com.ferelin.stockprice.shared.commonMain.ui.di.viewModelModule
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
