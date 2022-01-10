package com.ferelin.stockprice.di

import com.ferelin.core.ui.view.routing.RouterHost
import com.ferelin.stockprice.RouterHostImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
internal interface RouterHostModule {
  @Binds
  @Singleton
  fun routerHost(routerHostImpl: RouterHostImpl): RouterHost
}