package com.ferelin.core.data.di

import com.ferelin.core.data.R
import com.ferelin.core.data.api.*
import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceApi
import com.ferelin.core.data.entity.favouriteCompany.FavouriteCompanyApi
import com.ferelin.core.data.entity.favouriteCompany.FavouriteCompanyApiImpl
import com.ferelin.core.data.entity.news.NewsApi
import com.ferelin.core.data.entity.pastPrice.PastPricesApi
import com.ferelin.core.data.entity.stockPrice.StockPriceApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val networkModule = module {
  single(
    qualifier = named(STOCKS_RETROFIT)
  ) {
    RetrofitBuilder(
      baseUrl = STOCKS_BASE_URL,
      httpClient = OkHttpBuilder().build(),
      moshi = get()
    ).build()
  }
  single(
    qualifier = named(CRYPTOS_RETROFIT)
  ) {
    RetrofitBuilder(
      baseUrl = CRYPTOS_BASE_URL,
      httpClient = OkHttpBuilder().build(),
      moshi = get()
    ).build()
  }

  factory(
    qualifier = named(STOCKS_TOKEN)
  ) { androidContext().resources.getString(R.string.api_finnhub_token) }

  factory(
    qualifier = named(CRYPTOS_TOKEN)
  ) { androidContext().resources.getString(R.string.api_nomics_token) }

  factory<FavouriteCompanyApi> { FavouriteCompanyApiImpl(get()) }
  factory { get<Retrofit>(named(STOCKS_RETROFIT)).create(NewsApi::class.java) }
  factory { get<Retrofit>(named(STOCKS_RETROFIT)).create(PastPricesApi::class.java) }
  factory { get<Retrofit>(named(STOCKS_RETROFIT)).create(StockPriceApi::class.java) }
  factory { get<Retrofit>(named(CRYPTOS_RETROFIT)).create(CryptoPriceApi::class.java) }

  factory { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }

  single { FirebaseAuth.getInstance().apply { useAppLanguage() } }
  single { FirebaseDatabase.getInstance().reference }
}

private const val STOCKS_RETROFIT = "stocks-retrofit"
private const val CRYPTOS_RETROFIT = "cryptos-retrofit"