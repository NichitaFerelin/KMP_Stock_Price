package com.ferelin.core.domain.di

import com.ferelin.core.domain.usecase.*
import org.koin.dsl.module

val useCaseModule = module {
    factory<CompanyUseCase> { CompanyUseCaseImpl(get(), get()) }
    factory<CryptoPriceUseCase> { CryptoPriceUseCaseImpl(get(), get()) }
    factory<CryptoUseCase> { CryptoUseCaseImpl(get(), get()) }
    factory<NewsUseCase> { NewsUseCaseImpl(get(), get()) }
    factory<PastPricesUseCase> { PastPricesUseCaseImpl(get(), get()) }
    factory<SearchRequestsUseCase> { SearchRequestsUseCaseImpl(get(), get()) }
    factory<StockPriceUseCase> { StockPriceUseCaseImpl(get(), get()) }
}