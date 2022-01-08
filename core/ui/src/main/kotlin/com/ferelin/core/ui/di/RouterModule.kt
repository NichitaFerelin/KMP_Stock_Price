package com.ferelin.core.ui.di

import com.ferelin.core.ui.view.routing.AppRouter
import com.ferelin.core.ui.view.routing.Router
import dagger.Binds
import dagger.Module

@Module(includes = [RouterModuleBinds::class])
class RouterModule

@Module
internal interface RouterModuleBinds {
  @Binds
  fun router(appRouter: AppRouter): Router
}