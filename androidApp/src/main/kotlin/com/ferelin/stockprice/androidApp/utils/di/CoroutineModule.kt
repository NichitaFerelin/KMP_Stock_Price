package com.ferelin.stockprice.androidApp.utils.di

import com.ferelin.core.NAMED_EXTERNAL_SCOPE
import com.ferelin.stockprice.shared.ui.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coroutineModule = module {
  factory(
    qualifier = named(NAMED_EXTERNAL_SCOPE)
  ) {
    CoroutineScope(SupervisorJob() + get<DispatchersProvider>().IO)
  }
}