package com.ferelin.stockprice.di.modules

import com.ferelin.navigation.Router
import com.ferelin.navigation.ScreenResolver
import com.ferelin.stockprice.navigation.RouterImpl
import com.ferelin.stockprice.navigation.ScreenResolverImpl
import dagger.Binds
import dagger.Module

@Module
interface NavigationBindsModule {
  @Binds
  fun provideRouter(routerImpl: RouterImpl): Router

  @Binds
  fun provideScreenResolver(screenResolverImpl: ScreenResolverImpl): ScreenResolver
}