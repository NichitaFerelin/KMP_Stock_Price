package com.ferelin.core.data.di

import com.ferelin.core.NAMED_EXTERNAL_SCOPE
import com.ferelin.core.data.repository.*
import com.ferelin.core.domain.repository.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
  single<AuthUserStateRepository> {
    AuthUserStateRepositoryImpl(get())
  }
  single<FavouriteCompanyRepository> {
    FavouriteCompanyRepositoryImpl(get(), get(), get(), get(named(NAMED_EXTERNAL_SCOPE)), get())
  }
  factory<CryptoPriceRepository> {
    CryptoPriceRepositoryImpl(get(), get(), get(named(NAMED_CRYPTOS_TOKEN)))
  }
  factory<NewsRepository> {
    NewsRepositoryImpl(get(), get(), get(named(NAMED_STOCKS_TOKEN)))
  }
  factory<PastPriceRepository> {
    PastPriceRepositoryImpl(get(), get(), get(named(NAMED_STOCKS_TOKEN)))
  }
  factory<StockPriceRepository> {
    StockPriceRepositoryImpl(get(), get(), get(named(NAMED_STOCKS_TOKEN)))
  }

  factory<AuthRepository> { AuthRepositoryImpl(get()) }
  factory<CompanyRepository> { CompanyRepositoryImpl(get(), get(), get()) }
  factory<CryptoRepository> { CryptoRepositoryImpl(get(), get()) }
  factory<NotifyPriceRepository> { NotifyPriceRepositoryImpl(get()) }
  factory<ProfileRepository> { ProfileRepositoryImpl(get()) }
  factory<ProjectRepository> { ProjectRepositoryImpl(get()) }
  factory<SearchRequestsRepository> { SearchRequestsRepositoryImpl(get()) }
  factory<StoragePathRepository> { StoragePathRepositoryImpl(get()) }
}