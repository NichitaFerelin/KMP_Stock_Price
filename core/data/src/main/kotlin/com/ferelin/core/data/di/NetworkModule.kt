package com.ferelin.core.data.di

import android.content.Context
import com.ferelin.core.data.R
import com.ferelin.core.data.api.*
import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceApi
import com.ferelin.core.data.entity.favouriteCompany.FavouriteCompanyApi
import com.ferelin.core.data.entity.favouriteCompany.FavouriteCompanyApiImpl
import com.ferelin.core.data.entity.news.NewsApi
import com.ferelin.core.data.entity.pastPrice.PastPricesApi
import com.ferelin.core.data.entity.stockPrice.StockPriceApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [NetworkModuleBinds::class])
class NetworkModule {
  @Provides
  @Singleton
  @Named(STOCKS_RETROFIT)
  internal fun stocksRetrofit(moshi: Moshi): Retrofit {
    return RetrofitBuilder(
      baseUrl = STOCKS_BASE_URL,
      httpClient = OkHttpBuilder().build(),
      moshi = moshi
    ).build()
  }

  @Provides
  @Singleton
  @Named(CRYPTOS_RETROFIT)
  internal fun cryptosRetrofit(moshi: Moshi): Retrofit {
    return RetrofitBuilder(
      baseUrl = CRYPTOS_BASE_URL,
      httpClient = OkHttpBuilder().build(),
      moshi = moshi
    ).build()
  }

  @Provides
  @Named(STOCKS_TOKEN)
  fun stocksToken(context: Context): String {
    return context.resources.getString(R.string.api_finnhub_token)
  }

  @Provides
  @Named(CRYPTOS_TOKEN)
  fun cryptosToken(context: Context): String {
    return context.resources.getString(R.string.api_nomics_token)
  }

  @Provides
  @Reusable
  internal fun moshi(): Moshi {
    return Moshi.Builder()
      .add(KotlinJsonAdapterFactory())
      .build()
  }

  @Provides
  internal fun cryptoPriceApi(
    @Named(CRYPTOS_RETROFIT) retrofit: Retrofit
  ): CryptoPriceApi {
    return retrofit.create(CryptoPriceApi::class.java)
  }

  @Provides
  internal fun cryptoNewsApi(
    @Named(STOCKS_RETROFIT) retrofit: Retrofit
  ): NewsApi {
    return retrofit.create(NewsApi::class.java)
  }

  @Provides
  internal fun cryptoPastPriceApi(
    @Named(STOCKS_RETROFIT) retrofit: Retrofit
  ): PastPricesApi {
    return retrofit.create(PastPricesApi::class.java)
  }

  @Provides
  internal fun cryptoStockPriceApi(
    @Named(STOCKS_RETROFIT) retrofit: Retrofit
  ): StockPriceApi {
    return retrofit.create(StockPriceApi::class.java)
  }

  @Provides
  @Singleton
  internal fun firebaseAuth(): FirebaseAuth {
    return FirebaseAuth.getInstance()
      .apply { useAppLanguage() }
  }

  @Provides
  @Singleton
  internal fun firebaseReference(): DatabaseReference {
    return FirebaseDatabase.getInstance().reference
  }
}

@Suppress("unused")
@Module
internal interface NetworkModuleBinds {
  @Binds
  fun favouriteCompanyApi(impl: FavouriteCompanyApiImpl): FavouriteCompanyApi
}

private const val STOCKS_RETROFIT = "stocks-retrofit"
private const val CRYPTOS_RETROFIT = "cryptos-retrofit"