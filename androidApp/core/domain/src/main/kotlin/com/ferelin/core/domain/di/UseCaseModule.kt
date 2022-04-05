package com.ferelin.core.domain.di

import com.ferelin.core.NAMED_EXTERNAL_SCOPE
import com.ferelin.core.domain.usecase.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val useCaseModule = module {
  factory<AuthUseCase> {
    AuthUseCaseImpl(get(), get(), get(), get(named(NAMED_EXTERNAL_SCOPE)), get())
  }

  factory<DownloadProjectUseCase> { DownloadProjectUseCaseImpl(get()) }
  factory<StoragePathUseCase> { StoragePathUseCaseImpl(get(), get()) }
}