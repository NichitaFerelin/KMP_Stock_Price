package com.ferelin.core.di

import com.ferelin.core.ExternalScope
import com.ferelin.core.coroutine.DispatchersProvider
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@Module
class CoroutineModule {
  @Provides
  @Reusable
  fun dispatchersProvider(): DispatchersProvider {
    return DispatchersProvider()
  }

  @Provides
  @Reusable
  @ExternalScope
  fun externalScope(dispatchersProvider: DispatchersProvider): CoroutineScope {
    return CoroutineScope(SupervisorJob() + dispatchersProvider.IO)
  }
}