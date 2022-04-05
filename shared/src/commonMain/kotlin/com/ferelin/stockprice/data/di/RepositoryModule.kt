package com.ferelin.stockprice.data.di

import com.ferelin.common.domain.repository.*
import com.ferelin.stockprice.data.repository.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val repositoryModule = module {
  single<FavouriteCompanyRepository> {
    FavouriteCompanyRepositoryImpl(get())
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

  factory<CompanyRepository> { CompanyRepositoryImpl(get(), get(), get()) }
  factory<CryptoRepository> { CryptoRepositoryImpl(get(), get()) }
  factory<ProfileRepository> { ProfileRepositoryImpl(get()) }
  factory<SearchRequestsRepository> { SearchRequestsRepositoryImpl(get()) }
}