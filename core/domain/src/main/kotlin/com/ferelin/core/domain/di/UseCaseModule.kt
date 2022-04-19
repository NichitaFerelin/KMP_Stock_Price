package com.ferelin.core.domain.di

import com.ferelin.core.domain.usecase.*
import org.koin.dsl.module

val useCaseModule = module {
    single<CompanyUseCase> { CompanyUseCaseImpl(get(), get()) }
    single<CryptoPriceUseCase> { CryptoPriceUseCaseImpl(get(), get()) }
    single<CryptoUseCase> { CryptoUseCaseImpl(get(), get()) }
    single<NewsUseCase> { NewsUseCaseImpl(get(), get()) }
    single<SearchRequestsUseCase> { SearchRequestsUseCaseImpl(get(), get()) }
    single<StockPriceUseCase> { StockPriceUseCaseImpl(get(), get()) }
}