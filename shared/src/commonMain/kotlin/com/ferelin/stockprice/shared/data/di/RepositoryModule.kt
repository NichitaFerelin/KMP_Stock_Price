package com.ferelin.stockprice.shared.data.di

import com.ferelin.stockprice.androidApp.data.repository.CompanyRepositoryImpl
import com.ferelin.stockprice.androidApp.data.repository.CryptoPriceRepositoryImpl
import com.ferelin.stockprice.androidApp.data.repository.CryptoRepositoryImpl
import com.ferelin.stockprice.androidApp.data.repository.FavouriteCompanyRepositoryImpl
import com.ferelin.stockprice.androidApp.data.repository.NewsRepositoryImpl
import com.ferelin.stockprice.androidApp.data.repository.PastPriceRepositoryImpl
import com.ferelin.stockprice.androidApp.data.repository.ProfileRepositoryImpl
import com.ferelin.stockprice.androidApp.data.repository.SearchRequestsRepositoryImpl
import com.ferelin.stockprice.androidApp.data.repository.StockPriceRepositoryImpl
import com.ferelin.stockprice.androidApp.domain.repository.*
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