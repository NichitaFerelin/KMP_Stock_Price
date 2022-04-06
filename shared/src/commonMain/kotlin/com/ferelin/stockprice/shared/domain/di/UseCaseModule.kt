package com.ferelin.stockprice.shared.domain.di

import com.ferelin.stockprice.androidApp.domain.usecase.*
import com.ferelin.stockprice.androidApp.domain.usecase.CompanyUseCaseImpl
import com.ferelin.stockprice.androidApp.domain.usecase.CryptoPriceUseCaseImpl
import com.ferelin.stockprice.androidApp.domain.usecase.CryptoUseCaseImpl
import org.koin.dsl.module

internal val useCaseModule = module {
  factory<CompanyUseCase> { CompanyUseCaseImpl(get()) }
  factory<CryptoPriceUseCase> { CryptoPriceUseCaseImpl(get()) }
  factory<CryptoUseCase> { CryptoUseCaseImpl(get()) }
  factory<NewsUseCase> { NewsUseCaseImpl(get()) }
  factory<FavouriteCompanyUseCase> { FavouriteCompanyUseCaseImpl(get()) }
  factory<PastPricesUseCase> { PastPricesUseCaseImpl(get()) }
  factory<ProfileUseCase> { ProfileUseCaseImpl(get()) }
  factory<SearchRequestsUseCase> { SearchRequestsUseCaseImpl(get()) }
  factory<StockPriceUseCase> { StockPriceUseCaseImpl(get()) }
}