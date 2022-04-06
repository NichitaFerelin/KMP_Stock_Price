package com.ferelin.stockprice.androidApp.domain.di

import com.ferelin.core.NAMED_EXTERNAL_SCOPE
import com.ferelin.stockprice.androidApp.domain.usecase.*
import com.ferelin.stockprice.androidApp.domain.usecase.AuthUseCaseImpl
import com.ferelin.stockprice.androidApp.domain.usecase.DownloadProjectUseCaseImpl
import com.ferelin.stockprice.androidApp.domain.usecase.StoragePathUseCaseImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val nativeUseCaseModule = module {
  factory<AuthUseCase> {
    AuthUseCaseImpl(get(), get(), get(), get(named(NAMED_EXTERNAL_SCOPE)), get())
  }

  factory<DownloadProjectUseCase> { DownloadProjectUseCaseImpl(get()) }
  factory<StoragePathUseCase> { StoragePathUseCaseImpl(get(), get()) }
}