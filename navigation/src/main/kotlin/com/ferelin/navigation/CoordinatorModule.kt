package com.ferelin.navigation

import com.ferelin.core.ui.view.routing.Coordinator
import dagger.Binds
import dagger.Module

@Module(includes = [CoordinatorModuleBinds::class])
class CoordinatorModule

@Module
internal interface CoordinatorModuleBinds {
  @Binds
  fun coordinator(appCoordinator: AppCoordinator): Coordinator
}