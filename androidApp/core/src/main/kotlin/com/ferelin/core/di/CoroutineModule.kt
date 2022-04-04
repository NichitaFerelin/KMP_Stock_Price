package com.ferelin.core.di

import com.ferelin.core.NAMED_EXTERNAL_SCOPE
import com.ferelin.core.coroutine.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coroutineModule = module {
  factory { DispatchersProvider() }

  factory(
    qualifier = named(NAMED_EXTERNAL_SCOPE)
  ) {
    CoroutineScope(SupervisorJob() + get<DispatchersProvider>().IO)
  }
}