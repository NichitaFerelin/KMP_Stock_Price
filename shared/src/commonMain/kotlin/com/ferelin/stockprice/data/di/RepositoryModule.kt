package com.ferelin.stockprice.data.di

import com.ferelin.common.domain.repository.*
import com.ferelin.stockprice.data.repository.CompanyRepositoryImpl
import com.ferelin.stockprice.data.repository.CryptoPriceRepositoryImpl
import com.ferelin.stockprice.data.repository.CryptoRepositoryImpl
import com.ferelin.stockprice.data.repository.FavouriteCompanyRepositoryImpl
import com.ferelin.stockprice.data.repository.NewsRepositoryImpl
import com.ferelin.stockprice.data.repository.PastPriceRepositoryImpl
import com.ferelin.stockprice.data.repository.ProfileRepositoryImpl
import com.ferelin.stockprice.data.repository.SearchRequestsRepositoryImpl
import com.ferelin.stockprice.data.repository.StockPriceRepositoryImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val repositoryModule = module {
  single<FavouriteCompanyRepository> {
    FavouriteCompanyRepositoryImpl(get())
  }
  factory<CryptoPriceRepository> {
    CryptoPriceRepositoryImpl(get(), get(), get())
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

  factory<CompanyRepository> { CompanyRepositoryImpl(get(), get()) }
  factory<CryptoRepository> { CryptoRepositoryImpl(get()) }
  factory<ProfileRepository> { ProfileRepositoryImpl(get()) }
  factory<SearchRequestsRepository> { SearchRequestsRepositoryImpl(get()) }
}