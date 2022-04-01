package com.ferelin.core.domain.di

import com.ferelin.core.NAMED_EXTERNAL_SCOPE
import com.ferelin.core.domain.usecase.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val useCaseModule = module {
  factory<AuthUseCase> {
    AuthUseCaseImpl(get(), get(), get(), get(named(NAMED_EXTERNAL_SCOPE)), get())
  }

  factory<CompanyUseCase> { CompanyUseCaseImpl(get(), get()) }
  factory<CryptoPriceUseCase> { CryptoPriceUseCaseImpl(get(), get()) }
  factory<CryptoUseCase> { CryptoUseCaseImpl(get(), get()) }
  factory<DownloadProjectUseCase> { DownloadProjectUseCaseImpl(get()) }
  factory<FavouriteCompanyUseCase> { FavouriteCompanyUseCaseImpl(get(), get()) }
  factory<NewsUseCase> { NewsUseCaseImpl(get(), get()) }
  factory<NotifyPriceUseCase> { NotifyPriceUseCaseImpl(get(), get()) }
  factory<PastPricesUseCase> { PastPricesUseCaseImpl(get(), get()) }
  factory<ProfileUseCase> { ProfileUseCaseImpl(get(), get()) }
  factory<SearchRequestsUseCase> { SearchRequestsUseCaseImpl(get(), get()) }
  factory<StockPriceUseCase> { StockPriceUseCaseImpl(get(), get()) }
  factory<StoragePathUseCase> { StoragePathUseCaseImpl(get(), get()) }
}